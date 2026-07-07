package yuix.mm;

import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;

public final class AuthInputFactory {
    private final Context context;
    private final DensityScaler scaler;

    public AuthInputFactory(Context context, DensityScaler scaler) {
        this.context = context;
        this.scaler = scaler;
    }

    public EditText create(String hint) {
        EditText input = new EditText(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, scaler.dp(12), 0, 0);
        input.setLayoutParams(params);
        input.setSingleLine(true);
        input.setHint(hint);
        input.setTextColor(UiPalette.TEXT_PRIMARY);
        input.setHintTextColor(UiPalette.TEXT_MUTED);
        input.setTextSize(15);
        input.setBackgroundResource(R.drawable.bg_input_glass);
        return input;
    }
}
