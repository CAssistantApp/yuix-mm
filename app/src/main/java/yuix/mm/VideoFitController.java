package yuix.mm;

import android.media.MediaPlayer;
import android.view.ViewGroup;
import android.widget.VideoView;

public final class VideoFitController {
    private final VideoView videoView;
    private final int initialWidth;
    private final int initialHeight;

    public VideoFitController(VideoView videoView) {
        this.videoView = videoView;
        ViewGroup.LayoutParams params = videoView.getLayoutParams();
        initialWidth = params.width;
        initialHeight = params.height;
    }

    public void fit(MediaPlayer mediaPlayer) {
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        int screenWidth = videoView.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = videoView.getResources().getDisplayMetrics().heightPixels;
        if (videoWidth <= 0 || videoHeight <= 0 || screenWidth <= 0 || screenHeight <= 0) {
            return;
        }
        float scale = Math.max(screenWidth / (float) videoWidth, screenHeight / (float) videoHeight);
        ViewGroup.LayoutParams params = videoView.getLayoutParams();
        params.width = Math.round(videoWidth * scale);
        params.height = Math.round(videoHeight * scale);
        videoView.setLayoutParams(params);
    }

    public void reset() {
        ViewGroup.LayoutParams params = videoView.getLayoutParams();
        params.width = initialWidth;
        params.height = initialHeight;
        videoView.setLayoutParams(params);
    }
}
