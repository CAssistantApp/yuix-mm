package yuix.mm;

public final class RootScanReport {
    public final boolean rootGranted;
    public final int riskCount;
    public final String detail;

    public RootScanReport(boolean rootGranted, int riskCount, String detail) {
        this.rootGranted = rootGranted;
        this.riskCount = riskCount;
        this.detail = detail == null ? "" : detail;
    }

    public String title() {
        if (!rootGranted) {
            return "Root 未授权";
        }
        return riskCount == 0 ? "Root 管理器已就绪" : "已拦截危险代码";
    }
}
