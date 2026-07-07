package yuix.mm;

public interface PanelResultCallback {
    void onSuccess(String title, String detail);

    void onError(String title, String detail);
}
