package yuix.mm;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public final class PanelSslConfigurator {
    public void configure(HttpsURLConnection connection) throws Exception {
        TrustManager[] trustManagers = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagers, new SecureRandom());
        HostnameVerifier hostnameVerifier = (hostname, session) -> AppSecrets.PANEL_HOST.equals(hostname);
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setHostnameVerifier(hostnameVerifier);
    }
}
