package yuix.mm;

import android.app.Activity;

public final class MainViewBinder {
    public MainViews bind(Activity activity) {
        return new MainViews(
                activity.findViewById(R.id.statusText),
                activity.findViewById(R.id.detailText),
                activity.findViewById(R.id.videoView),
                activity.findViewById(R.id.skipHint),
                activity.findViewById(R.id.homeLayer),
                activity.findViewById(R.id.bottomDock),
                activity.findViewById(R.id.rootLayer),
                activity.findViewById(R.id.videoSkipLayer));
    }
}
