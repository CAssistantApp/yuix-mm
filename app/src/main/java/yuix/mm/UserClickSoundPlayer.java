package yuix.mm;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;

public final class UserClickSoundPlayer {
    private static final int SAMPLE_RATE = 44100;
    private final AudioTrack audioTrack;
    private boolean released;
    private boolean available;

    public UserClickSoundPlayer() {
        this(null);
    }

    public UserClickSoundPlayer(Context context) {
        AudioTrack track = null;
        try {
            byte[] sound = createClickSound();
            track = new AudioTrack.Builder()
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build())
                    .setAudioFormat(new AudioFormat.Builder()
                            .setSampleRate(SAMPLE_RATE)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build())
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .setBufferSizeInBytes(sound.length)
                    .build();
            int written = track.write(sound, 0, sound.length);
            available = written > 0;
        } catch (RuntimeException exception) {
            if (track != null) {
                track.release();
            }
            track = null;
            available = false;
        }
        audioTrack = track;
    }

    public void play() {
        if (released || !available || audioTrack == null) {
            return;
        }
        try {
            audioTrack.pause();
            audioTrack.setPlaybackHeadPosition(0);
            audioTrack.play();
        } catch (IllegalStateException ignored) {
        }
    }

    public void release() {
        if (released) {
            return;
        }
        released = true;
        available = false;
        if (audioTrack != null) {
            audioTrack.release();
        }
    }

    private static byte[] createClickSound() {
        int samples = SAMPLE_RATE * 46 / 1000;
        byte[] data = new byte[samples * 2];
        for (int index = 0; index < samples; index++) {
            double time = index / (double) SAMPLE_RATE;
            double envelope = Math.exp(-time * 92d);
            double tick = Math.sin(2d * Math.PI * 2200d * time) * envelope;
            double secondTick = 0d;
            if (time > 0.018d) {
                double shifted = time - 0.018d;
                secondTick = Math.sin(2d * Math.PI * 1650d * shifted) * Math.exp(-shifted * 120d) * 0.58d;
            }
            short value = (short) (Math.max(-1d, Math.min(1d, tick + secondTick)) * Short.MAX_VALUE * 0.42d);
            int offset = index * 2;
            data[offset] = (byte) (value & 0xFF);
            data[offset + 1] = (byte) ((value >> 8) & 0xFF);
        }
        return data;
    }
}
