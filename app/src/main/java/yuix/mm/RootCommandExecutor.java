package yuix.mm;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public final class RootCommandExecutor {
    public RootCommandResult execute(String command, long timeoutMillis) throws Exception {
        Process process = new ProcessBuilder("su", "-c", command)
                .redirectErrorStream(true)
                .start();
        ExecutorService readerExecutor = Executors.newSingleThreadExecutor();
        Future<String> outputFuture = readerExecutor.submit(new OutputReader(process.getInputStream()));
        try {
            boolean finished = process.waitFor(timeoutMillis, TimeUnit.MILLISECONDS);
            if (!finished) {
                process.destroyForcibly();
                return new RootCommandResult(false, -1, "Root command timed out");
            }
            int exitCode = process.exitValue();
            String output = outputFuture.get(1000L, TimeUnit.MILLISECONDS).trim();
            return new RootCommandResult(exitCode == 0, exitCode, output);
        } finally {
            readerExecutor.shutdownNow();
        }
    }

    private static final class OutputReader implements Callable<String> {
        private final InputStream inputStream;

        private OutputReader(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public String call() throws Exception {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            return output.toString(StandardCharsets.UTF_8.name());
        }
    }
}
