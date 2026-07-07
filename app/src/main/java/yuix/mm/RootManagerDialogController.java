package yuix.mm;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;

public final class RootManagerDialogController {
    private final Context context;
    private final DensityScaler scaler;
    private final InfoLineFactory infoLineFactory;
    private final DialogStyler dialogStyler;
    private final RootSecurityScanner scanner;
    private final ExecutorService executorService;
    private final UserClickSoundBinder clickSoundBinder;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public RootManagerDialogController(Context context,
                                       DensityScaler scaler,
                                       InfoLineFactory infoLineFactory,
                                       DialogStyler dialogStyler,
                                       RootSecurityScanner scanner,
                                       ExecutorService executorService,
                                       UserClickSoundBinder clickSoundBinder) {
        this.context = context;
        this.scaler = scaler;
        this.infoLineFactory = infoLineFactory;
        this.dialogStyler = dialogStyler;
        this.scanner = scanner;
        this.executorService = executorService;
        this.clickSoundBinder = clickSoundBinder;
    }

    public void show() {
        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(scaler.dp(24), scaler.dp(20), scaler.dp(24), scaler.dp(18));

        TextView title = new TextView(context);
        title.setText("Root Manager");
        title.setTextColor(UiPalette.TEXT_PRIMARY);
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        content.addView(title);

        TextView status = infoLineFactory.create("状态", "等待 Root 授权与安全扫描");
        content.addView(status);

        TextView detail = new TextView(context);
        detail.setText("点击“开始扫描”后会请求 Root 权限，并扫描可疑进程、危险命令特征和常见注入文件。发现风险时会停止后续自动操作。");
        detail.setTextColor(UiPalette.TEXT_SECONDARY);
        detail.setTextSize(13);
        detail.setLineSpacing(scaler.dp(3), 1f);
        detail.setPadding(0, scaler.dp(12), 0, 0);

        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(detail);
        content.addView(scrollView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                scaler.dp(220)));

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(content)
                .setNegativeButton("关闭", null)
                .setPositiveButton("开始扫描", null)
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            dialogStyler.apply(dialog);
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            clickSoundBinder.bind(positiveButton);
            clickSoundBinder.bind(negativeButton);
            positiveButton.setOnClickListener(view -> runScan(status, detail, positiveButton));
        });
        dialog.show();
    }

    private void runScan(TextView status, TextView detail, Button scanButton) {
        scanButton.setEnabled(false);
        status.setText("状态  ·  正在请求 Root 并扫描");
        detail.setText("Root 安全扫描运行中，请在授权弹窗中允许本应用。扫描命令均设置超时，避免长时间卡住。");
        executorService.execute(() -> {
            RootScanReport report = scanner.scan();
            mainHandler.post(() -> {
                scanButton.setEnabled(true);
                status.setText("状态  ·  " + report.title());
                detail.setText(report.detail);
            });
        });
    }
}
