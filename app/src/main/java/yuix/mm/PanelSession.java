package yuix.mm;

public final class PanelSession {
    private String cookie = "";

    public String getCookie() {
        return cookie;
    }

    public boolean hasCookie() {
        return !cookie.isEmpty();
    }

    public void updateCookie(String cookie) {
        if (cookie != null && !cookie.isEmpty()) {
            this.cookie = cookie;
        }
    }
}
