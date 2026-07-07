package yuix.mm;

import android.view.View;

public final class HomeStateController {
    private final MainViews views;
    private final HomeAnimationController animationController;

    public HomeStateController(MainViews views) {
        this(views, null);
    }

    public HomeStateController(MainViews views, HomeAnimationController animationController) {
        this.views = views;
        this.animationController = animationController;
    }

    public void showLocked() {
        views.homeLayer.setAlpha(0f);
        views.bottomDock.setAlpha(0f);
        views.homeLayer.setVisibility(View.GONE);
        views.bottomDock.setVisibility(View.GONE);
        views.videoView.setVisibility(View.GONE);
        views.skipHint.setVisibility(View.GONE);
        views.videoSkipLayer.setVisibility(View.GONE);
        views.statusText.setText("");
        views.detailText.setText("");
    }

    public void showBusy(String title, String detail) {
        views.statusText.setText(title);
        views.detailText.setText(detail);
    }

    public void showHome(String title, String detail) {
        views.videoView.stopPlayback();
        views.videoView.setVisibility(View.GONE);
        views.skipHint.setVisibility(View.GONE);
        views.videoSkipLayer.setVisibility(View.GONE);
        views.homeLayer.setVisibility(View.VISIBLE);
        views.bottomDock.setVisibility(View.VISIBLE);
        views.statusText.setText(title);
        views.detailText.setText(detail);
        if (animationController != null) {
            animationController.playOpening();
        } else {
            views.homeLayer.setAlpha(0f);
            views.bottomDock.setAlpha(0f);
            views.homeLayer.animate().alpha(1f).setDuration(260).start();
            views.bottomDock.animate().alpha(1f).setDuration(260).start();
        }
    }
}
