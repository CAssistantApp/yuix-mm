package yuix.mm;

import android.view.View;
import android.view.ViewGroup;

public final class HomeAnimationController {
    private final MainViews views;

    public HomeAnimationController(MainViews views) {
        this.views = views;
    }

    public void playOpening() {
        animateHomeLayer();
        animateDock();
    }

    private void animateHomeLayer() {
        views.homeLayer.animate().cancel();
        views.homeLayer.setAlpha(0f);
        views.homeLayer.setTranslationY(28f);
        views.homeLayer.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(80)
                .setDuration(520)
                .start();
    }

    private void animateDock() {
        views.bottomDock.animate().cancel();
        views.bottomDock.setAlpha(0f);
        views.bottomDock.setTranslationY(36f);
        views.bottomDock.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(180)
                .setDuration(520)
                .start();
        animateDockButtons();
    }

    private void animateDockButtons() {
        if (!(views.bottomDock instanceof ViewGroup)) {
            return;
        }
        ViewGroup dock = (ViewGroup) views.bottomDock;
        for (int index = 0; index < dock.getChildCount(); index++) {
            View child = dock.getChildAt(index);
            child.animate().cancel();
            child.setAlpha(0f);
            child.setScaleX(0.86f);
            child.setScaleY(0.86f);
            child.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(260L + index * 42L)
                    .setDuration(360)
                    .start();
        }
    }
}
