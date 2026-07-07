package yuix.mm;

import android.content.Context;

public final class DensityScaler {
    private final Context context;

    public DensityScaler(Context context) {
        this.context = context;
    }

    public int dp(int value) {
        return (int) (value * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}
