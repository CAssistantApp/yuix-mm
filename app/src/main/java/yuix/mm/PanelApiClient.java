package yuix.mm;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public final class PanelApiClient {
    private final PanelSession session;
    private final PanelSslConfigurator sslConfigurator;
    private final JsonBodyFactory bodyFactory;

    public PanelApiClient(PanelSession session, PanelSslConfigurator sslConfigurator, JsonBodyFactory bodyFactory) {
        this.session = session;
        this.sslConfigurator = sslConfigurator;
        this.bodyFactory = bodyFactory;
    }

    public JSONObject login(String path) throws Exception {
        return postJson(path, bodyFactory.loginBody());
    }

    public JSONObject request(String path) throws Exception {
        return postJson(path, bodyFactory.actionBody(path));
    }

    private JSONObject postJson(String path, JSONObject body) throws Exception {
        URL url = new URL(AppSecrets.PANEL_BASE_URL + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            if (connection instanceof HttpsURLConnection) {
                sslConfigurator.configure((HttpsURLConnection) connection);
            }
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(12000);
            connection.setReadTimeout(12000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json,text/plain,*/*");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            if (session.hasCookie()) {
                connection.setRequestProperty("Cookie", session.getCookie());
            }

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(body.toString().getBytes(StandardCharsets.UTF_8));
            }

            session.updateCookie(connection.getHeaderField("Set-Cookie"));
            int code = connection.getResponseCode();
            String text = readResponse(connection, code).trim();
            if (text.startsWith("{")) {
                return new JSONObject(text);
            }

            JSONObject result = new JSONObject();
            result.put("code", code);
            result.put("message", text.isEmpty() ? "服务器已响应，但没有返回内容" : text);
            return result;
        } finally {
            connection.disconnect();
        }
    }

    private String readResponse(HttpURLConnection connection, int code) throws Exception {
        InputStream stream = code >= 200 && code < 400 ? connection.getInputStream() : connection.getErrorStream();
        if (stream == null) {
            return "";
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
}
