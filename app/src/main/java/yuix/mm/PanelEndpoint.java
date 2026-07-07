package yuix.mm;

public final class PanelEndpoint {
    public final String title;
    public final String path;
    public final boolean login;

    public PanelEndpoint(String title, String path, boolean login) {
        this.title = title;
        this.path = path;
        this.login = login;
    }
}
