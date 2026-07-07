package yuix.mm;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

public final class DockActionBinder {
    private final Activity activity;
    private final PanelActionRunner runner;
    private final PanelStatusPresenter presenter;
    private final AuthGate authGate;
    private final UserClickSoundBinder clickSoundBinder;

    public DockActionBinder(Activity activity, PanelActionRunner runner, PanelStatusPresenter presenter,
                            AuthGate authGate, UserClickSoundBinder clickSoundBinder) {
        this.activity = activity;
        this.runner = runner;
        this.presenter = presenter;
        this.authGate = authGate;
        this.clickSoundBinder = clickSoundBinder;
    }

    public void bind(int viewId, PanelEndpoint endpoint) {
        View action = activity.findViewById(viewId);
        if (action == null) {
            return;
        }
        clickSoundBinder.bind(action);
        action.setOnClickListener(view -> {
            if (!authGate.isAuthenticated()) {
                Toast.makeText(activity, "请先完成验证", Toast.LENGTH_SHORT).show();
                return;
            }
            presenter.showBusy(endpoint.title + "中…", "secure request");
            runner.run(endpoint, presenter);
        });
    }
}
