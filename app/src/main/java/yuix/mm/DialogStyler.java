package yuix.mm;

import android.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;

public final class DialogStyler {
    private final DensityScaler scaler;
    private final DeviceProfile profile;

    public DialogStyler(DensityScaler scaler, DeviceProfile profile) {
        this.scaler = scaler;
        this.profile = profile;
    }

    public void apply(AlertDialog dialog) {
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setBackgroundDrawableResource(R.drawable.bg_dialog_glow);
        WindowManager.LayoutParams params = window.getAttributes();
        int maxWidth = scaler.dp(profile.isTablet() ? 420 : 340);
        params.width = Math.min(window.getContext().getResources().getDisplayMetrics().widthPixels - scaler.dp(48), maxWidth);
        window.setAttributes(params);
    }
}
