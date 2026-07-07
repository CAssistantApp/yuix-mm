package yuix.mm;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.LinearLayout;

public final class SettingsDialogController {
    private final Activity activity;
    private final DensityScaler scaler;
    private final DeviceProfile profile;
    private final InfoLineFactory infoLineFactory;
    private final DialogStyler dialogStyler;
    private final UserClickSoundBinder clickSoundBinder;

    public SettingsDialogController(Activity activity, DensityScaler scaler, DeviceProfile profile,
                                    InfoLineFactory infoLineFactory, DialogStyler dialogStyler,
                                    UserClickSoundBinder clickSoundBinder) {
        this.activity = activity;
        this.scaler = scaler;
        this.profile = profile;
        this.infoLineFactory = infoLineFactory;
        this.dialogStyler = dialogStyler;
        this.clickSoundBinder = clickSoundBinder;
    }

    public void show(String currentVideoName, Runnable replayAction) {
        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(scaler.dp(4), scaler.dp(8), scaler.dp(4), 0);

        content.addView(infoLineFactory.create("设备模式", profile.orientationLabel()));
        content.addView(infoLineFactory.create("启动视频", profile.introVideoName()));
        content.addView(infoLineFactory.create("当前状态", currentVideoName.isEmpty() ? "等待验证" : "已载入 " + currentVideoName));
        content.addView(infoLineFactory.create("界面风格", "简洁浅色"));

        AlertDialog dialog = new AlertDialog.Builder(activity, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog)
                .setTitle("设置")
                .setView(content)
                .setPositiveButton("重播", (d, which) -> replayAction.run())
                .setNegativeButton("关闭", null)
                .create();
        dialog.setOnShowListener(d -> {
            dialogStyler.apply(dialog);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(UiPalette.TEXT_PRIMARY);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(UiPalette.TEXT_MUTED);
            clickSoundBinder.bind(dialog.getButton(AlertDialog.BUTTON_POSITIVE));
            clickSoundBinder.bind(dialog.getButton(AlertDialog.BUTTON_NEGATIVE));
        });
        dialog.show();
    }
}
