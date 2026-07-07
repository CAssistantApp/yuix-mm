package yuix.mm;

public interface VideoCompletionListener {
    void onVideoFinished(String videoName);

    void onVideoUnavailable(String videoName);
}
