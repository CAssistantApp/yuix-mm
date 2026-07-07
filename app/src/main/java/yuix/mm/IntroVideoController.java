package yuix.mm;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.VideoView;

import java.util.concurrent.ExecutorService;

public final class IntroVideoController {
    private final DeviceProfile profile;
    private final MainViews views;
    private final HomeStateController homeStateController;
    private final VideoFitController fitController;
    private final UserClickSoundBinder clickSoundBinder;
    private final AssetVideoCache videoCache;
    private final ExecutorService executorService;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private String currentVideoName = "";
    private boolean isPlaying;

    public IntroVideoController(DeviceProfile profile,
                                MainViews views,
                                HomeStateController homeStateController,
                                VideoFitController fitController,
                                UserClickSoundBinder clickSoundBinder,
                                AssetVideoCache videoCache,
                                ExecutorService executorService) {
        this.profile = profile;
        this.views = views;
        this.homeStateController = homeStateController;
        this.fitController = fitController;
        this.clickSoundBinder = clickSoundBinder;
        this.videoCache = videoCache;
        this.executorService = executorService;
    }

    public void play(VideoCompletionListener listener) {
        currentVideoName = profile.introVideoName();
        isPlaying = true;
        views.statusText.setText("已验证");
        views.detailText.setText(profile.introDetail());
        views.homeLayer.animate().cancel();
        views.bottomDock.animate().cancel();
        views.homeLayer.setAlpha(0f);
        views.bottomDock.setAlpha(0f);
        views.homeLayer.setVisibility(View.GONE);
        views.bottomDock.setVisibility(View.GONE);
        showSkipHint();
        prepareVideoView(listener);
        executorService.execute(() -> {
            try {
                Uri videoUri = videoCache.uriFor(currentVideoName);
                mainHandler.post(() -> startVideo(videoUri, listener));
            } catch (Exception exception) {
                mainHandler.post(() -> failPlayback(listener, "视频服务失败", exception.getMessage()));
            }
        });
    }

    public void stop() {
        clearPlaybackState();
        views.videoView.stopPlayback();
    }

    public String getCurrentVideoName() {
        return currentVideoName;
    }

    private void prepareVideoView(VideoCompletionListener listener) {
        VideoView videoView = views.videoView;
        videoView.setVisibility(View.VISIBLE);
        videoView.setAlpha(0f);
        videoView.animate().alpha(1f).setDuration(320).start();
        videoView.setClickable(true);
        views.videoSkipLayer.setVisibility(View.VISIBLE);
        views.videoSkipLayer.bringToFront();
        views.skipHint.bringToFront();
        clickSoundBinder.bind(videoView);
        videoView.setOnClickListener(view -> skip(listener));
        views.videoSkipLayer.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                clickSoundBinder.play();
                skip(listener);
            }
            return true;
        });
        videoView.setOnPreparedListener(mediaPlayer -> {
            fitController.fit(mediaPlayer);
            mediaPlayer.setOnInfoListener((player, what, extra) -> true);
        });
        videoView.setOnCompletionListener(mediaPlayer -> finishPlayback(listener));
        videoView.setOnErrorListener((mediaPlayer, what, extra) -> {
            failPlayback(listener, "视频解码失败", "what=" + what + ", extra=" + extra);
            return true;
        });
    }

    private void startVideo(Uri videoUri, VideoCompletionListener listener) {
        if (!isPlaying) {
            return;
        }
        try {
            views.videoView.setVideoURI(videoUri);
            views.videoView.start();
        } catch (RuntimeException exception) {
            failPlayback(listener, "视频启动失败", exception.getMessage());
        }
    }

    private void skip(VideoCompletionListener listener) {
        if (!isPlaying) {
            return;
        }
        views.videoView.stopPlayback();
        finishPlayback(listener);
    }

    private void finishPlayback(VideoCompletionListener listener) {
        if (!isPlaying) {
            return;
        }
        clearPlaybackState();
        homeStateController.showHome("YUIX", "ready · " + currentVideoName);
        listener.onVideoFinished(currentVideoName);
    }

    private void failPlayback(VideoCompletionListener listener, String title, String detail) {
        clearPlaybackState();
        String message = detail == null || detail.isEmpty() ? currentVideoName : currentVideoName + " · " + detail;
        homeStateController.showHome(title, message);
        listener.onVideoUnavailable(currentVideoName);
    }

    private void showSkipHint() {
        views.skipHint.setAlpha(0f);
        views.skipHint.setVisibility(View.VISIBLE);
        views.skipHint.animate().alpha(1f).setDuration(240).start();
    }

    private void clearPlaybackState() {
        isPlaying = false;
        views.videoView.animate().cancel();
        views.videoView.setOnClickListener(null);
        views.videoView.setOnTouchListener(null);
        views.videoSkipLayer.setOnTouchListener(null);
        views.videoSkipLayer.setVisibility(View.GONE);
        views.videoView.setOnPreparedListener(null);
        views.videoView.setOnCompletionListener(null);
        views.videoView.setOnErrorListener(null);
        views.videoView.setAlpha(0f);
        views.videoView.setVisibility(View.GONE);
        fitController.reset();
        views.skipHint.animate().cancel();
        views.skipHint.setVisibility(View.GONE);
    }
}
