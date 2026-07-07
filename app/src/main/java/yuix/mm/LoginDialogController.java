package yuix.mm;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public final class LoginDialogController {
    private final Activity activity;
    private final DensityScaler scaler;
    private final AuthInputFactory inputFactory;
    private final CredentialValidator validator;
    private final DialogStyler dialogStyler;
    private final UserClickSoundBinder clickSoundBinder;

    public LoginDialogController(Activity activity, DensityScaler scaler, AuthInputFactory inputFactory,
                                 CredentialValidator validator, DialogStyler dialogStyler,
                                 UserClickSoundBinder clickSoundBinder) {
        this.activity = activity;
        this.scaler = scaler;
        this.inputFactory = inputFactory;
        this.validator = validator;
        this.dialogStyler = dialogStyler;
        this.clickSoundBinder = clickSoundBinder;
    }

    public void show(AuthResultListener listener) {
        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(scaler.dp(6), scaler.dp(8), scaler.dp(6), 0);

        TextView hint = new TextView(activity);
        hint.setText("YUIX Secure Gate");
        hint.setTextColor(UiPalette.TEXT_SECONDARY);
        hint.setTextSize(13);
        hint.setGravity(Gravity.CENTER);
        content.addView(hint, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        EditText usernameInput = inputFactory.create("账户");
        EditText passwordInput = inputFactory.create("密码");
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        content.addView(usernameInput);
        content.addView(passwordInput);

        AlertDialog dialog = new AlertDialog.Builder(activity, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog)
                .setTitle("验证")
                .setView(content)
                .setCancelable(false)
                .setPositiveButton("验证", null)
                .setNegativeButton("退出", (d, which) -> activity.finish())
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(d -> activity.finish());
        dialog.setOnShowListener(d -> {
            dialogStyler.apply(dialog);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(UiPalette.TEXT_PRIMARY);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(UiPalette.TEXT_MUTED);
            clickSoundBinder.bind(dialog.getButton(AlertDialog.BUTTON_POSITIVE));
            clickSoundBinder.bind(dialog.getButton(AlertDialog.BUTTON_NEGATIVE));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
                String username = usernameInput.getText().toString().trim();
                String password = passwordInput.getText().toString();
                if (validator.isValid(username, password)) {
                    dialog.dismiss();
                    listener.onAuthenticated();
                } else {
                    Toast.makeText(activity, "验证失败", Toast.LENGTH_SHORT).show();
                }
            });
        });
        dialog.show();
    }
}
