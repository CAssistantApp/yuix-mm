package yuix.mm;

public final class AuthGate {
    private boolean authenticated;

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void unlock() {
        authenticated = true;
    }
}
