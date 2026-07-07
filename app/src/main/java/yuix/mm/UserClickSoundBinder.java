package yuix.mm;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public final class UserClickSoundBinder {
    private final UserClickSoundPlayer player;

    public UserClickSoundBinder(UserClickSoundPlayer player) {
        this.player = player;
    }

    public void bind(View view) {
        if (view == null) {
            return;
        }
        view.setOnTouchListener((target, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && target.isEnabled() && target.isClickable()) {
                play();
            }
            return false;
        });
    }

    public void play() {
        player.play();
    }

    public void release() {
        player.release();
    }

    public void bindChildren(ViewGroup group) {
        if (group == null) {
            return;
        }
        for (int index = 0; index < group.getChildCount(); index++) {
            View child = group.getChildAt(index);
            if (child.isClickable()) {
                bind(child);
            }
            if (child instanceof ViewGroup) {
                bindChildren((ViewGroup) child);
            }
        }
    }
}
