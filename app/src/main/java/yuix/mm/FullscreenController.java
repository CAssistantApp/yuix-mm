package yuix.mm;

import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

public final class FullscreenController {
    private final Window window;

    public FullscreenController(Window window) {
        this.window = window;
    }

    public void enable() {
        window.setDecorFitsSystemWindows(false);
        WindowInsetsController controller = window.getInsetsController();
        if (controller != null) {
            controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }
}
