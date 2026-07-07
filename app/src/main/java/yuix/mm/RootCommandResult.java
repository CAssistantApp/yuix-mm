package yuix.mm;

public final class RootCommandResult {
    public final boolean success;
    public final int exitCode;
    public final String output;

    public RootCommandResult(boolean success, int exitCode, String output) {
        this.success = success;
        this.exitCode = exitCode;
        this.output = output == null ? "" : output;
    }
}
