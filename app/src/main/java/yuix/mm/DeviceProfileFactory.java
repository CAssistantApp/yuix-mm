package yuix.mm;

import android.content.Context;
import android.content.res.Configuration;

public final class DeviceProfileFactory {
    public DeviceProfile create(Context context) {
        boolean tablet = (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        return new DeviceProfile(tablet);
    }
}
