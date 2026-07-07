package yuix.mm;

public final class CredentialValidator {
    public boolean isValid(String username, String password) {
        return AppSecrets.APP_LOGIN_USERNAME.equals(username) && AppSecrets.APP_LOGIN_PASSWORD.equals(password);
    }
}
