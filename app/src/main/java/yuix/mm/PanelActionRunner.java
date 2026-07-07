package yuix.mm;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;

public final class PanelActionRunner {
    private final ExecutorService executorService;
    private final PanelApiClient apiClient;
    private final AuthGate authGate;

    public PanelActionRunner(ExecutorService executorService, PanelApiClient apiClient, AuthGate authGate) {
        this.executorService = executorService;
        this.apiClient = apiClient;
        this.authGate = authGate;
    }

    public void run(PanelEndpoint endpoint, PanelResultCallback callback) {
        if (!authGate.isAuthenticated()) {
            callback.onError("验证未完成", "请先完成验证");
            return;
        }
        executorService.execute(() -> {
            try {
                JSONObject result = endpoint.login ? apiClient.login(endpoint.path) : apiClient.request(endpoint.path);
                String message = result.optString("message", result.toString());
                callback.onSuccess(endpoint.title + "完成", message);
            } catch (Exception exception) {
                callback.onError(endpoint.title + "失败", exception.getMessage());
            }
        });
    }
}
