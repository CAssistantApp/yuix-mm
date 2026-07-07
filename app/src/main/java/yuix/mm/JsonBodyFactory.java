package yuix.mm;

import org.json.JSONObject;

public final class JsonBodyFactory {
    public JSONObject loginBody() throws Exception {
        JSONObject body = new JSONObject();
        body.put("username", AppSecrets.PANEL_USERNAME);
        body.put("password", AppSecrets.PANEL_PASSWORD);
        return body;
    }

    public JSONObject actionBody(String path) throws Exception {
        JSONObject body = new JSONObject();
        body.put("username", AppSecrets.PANEL_USERNAME);
        body.put("action", path.replace("/api/", ""));
        return body;
    }
}
