package yuix.mm;

import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

public final class MainViews {
    public final TextView statusText;
    public final TextView detailText;
    public final VideoView videoView;
    public final View skipHint;
    public final View homeLayer;
    public final View bottomDock;
    public final View rootLayer;
    public final View videoSkipLayer;

    public MainViews(TextView statusText, TextView detailText, VideoView videoView, View skipHint, View homeLayer, View bottomDock, View rootLayer, View videoSkipLayer) {
        this.statusText = statusText;
        this.detailText = detailText;
        this.videoView = videoView;
        this.skipHint = skipHint;
        this.homeLayer = homeLayer;
        this.bottomDock = bottomDock;
        this.rootLayer = rootLayer;
        this.videoSkipLayer = videoSkipLayer;
    }
}
