package yuix.mm;

import android.content.Context;
import android.widget.TextView;

public final class InfoLineFactory {
    private final Context context;
    private final DensityScaler scaler;

    public InfoLineFactory(Context context, DensityScaler scaler) {
        this.context = context;
        this.scaler = scaler;
    }

    public TextView create(String label, String value) {
        TextView textView = new TextView(context);
        textView.setText(label + "  ·  " + value);
        textView.setTextColor(UiPalette.TEXT_SECONDARY);
        textView.setTextSize(14);
        textView.setPadding(0, scaler.dp(8), 0, scaler.dp(2));
        return textView;
    }
}
