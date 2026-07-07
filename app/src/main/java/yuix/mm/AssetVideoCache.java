package yuix.mm;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public final class AssetVideoCache {
    private final Context context;

    public AssetVideoCache(Context context) {
        this.context = context.getApplicationContext();
    }

    public Uri uriFor(String assetName) throws Exception {
        File directory = new File(context.getCacheDir(), "intro_video");
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IllegalStateException("Unable to create video cache directory");
        }
        File output = new File(directory, assetName);
        if (!output.exists() || output.length() == 0L) {
            copy(assetName, output);
        }
        return Uri.fromFile(output);
    }

    private void copy(String assetName, File output) throws Exception {
        File temporary = new File(output.getParentFile(), assetName + ".tmp");
        if (temporary.exists() && !temporary.delete()) {
            throw new IllegalStateException("Unable to clean temporary video cache file");
        }
        try (InputStream inputStream = context.getAssets().open(assetName);
             FileOutputStream outputStream = new FileOutputStream(temporary)) {
            byte[] buffer = new byte[64 * 1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.getFD().sync();
        }
        if (output.exists() && !output.delete()) {
            throw new IllegalStateException("Unable to replace video cache file");
        }
        if (!temporary.renameTo(output)) {
            throw new IllegalStateException("Unable to prepare video cache file");
        }
    }
}
