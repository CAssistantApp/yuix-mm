package yuix.mm;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public final class PanelStatusPresenter implements PanelResultCallback {
    private final Context context;
    private final Handler mainHandler;
    private final HomeStateController homeStateController;

    public PanelStatusPresenter(Context context, Handler mainHandler, HomeStateController homeStateController) {
        this.context = context;
        this.mainHandler = mainHandler;
        this.homeStateController = homeStateController;
    }

    public void showBusy(String title, String detail) {
        homeStateController.showBusy(title, detail);
    }

    @Override
    public void onSuccess(String title, String detail) {
        mainHandler.post(() -> {
            homeStateController.showBusy(title, detail);
            Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onError(String title, String detail) {
        mainHandler.post(() -> {
            String safeDetail = detail == null || detail.isEmpty() ? "请确认面板 API 路径是否开放" : detail;
            homeStateController.showBusy(title, safeDetail);
            Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
        });
    }
}
