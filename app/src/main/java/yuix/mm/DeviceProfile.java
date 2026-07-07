package yuix.mm;

public final class DeviceProfile {
    private final boolean tablet;

    public DeviceProfile(boolean tablet) {
        this.tablet = tablet;
    }

    public boolean isTablet() {
        return tablet;
    }

    public String orientationLabel() {
        return tablet ? "平板横屏" : "手机竖屏";
    }

    public String introVideoName() {
        return tablet ? "2.mp4" : "1.mp4";
    }

    public String introDetail() {
        return tablet ? "tablet landscape · intro 2.mp4" : "phone portrait · intro 1.mp4";
    }
}
