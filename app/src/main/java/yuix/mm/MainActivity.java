package yuix.mm;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import android.app.Activity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private DeviceProfile profile;
    private HomeStateController homeStateController;
    private IntroVideoController introVideoController;
    private SettingsDialogController settingsDialogController;
    private AuthGate authGate;
    private UserClickSoundBinder clickSoundBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profile = new DeviceProfileFactory().create(this);
        authGate = new AuthGate();
        DensityScaler scaler = new DensityScaler(this);
        clickSoundBinder = new UserClickSoundBinder(new UserClickSoundPlayer(this));
        MainViews views = new MainViewBinder().bind(this);
        homeStateController = new HomeStateController(views);
        introVideoController = new IntroVideoController(
                profile,
                views,
                homeStateController,
                new VideoFitController(views.videoView),
                clickSoundBinder,
                new AssetVideoCache(this),
                executorService);
        DialogStyler dialogStyler = new DialogStyler(scaler, profile);
        settingsDialogController = new SettingsDialogController(
                this,
                scaler,
                profile,
                new InfoLineFactory(this, scaler),
                dialogStyler,
                clickSoundBinder);

        new FullscreenController(getWindow()).enable();
        homeStateController.showLocked();
        bindActions();
        showLogin(dialogStyler, scaler);
    }

    private void showLogin(DialogStyler dialogStyler, DensityScaler scaler) {
        LoginDialogController loginDialogController = new LoginDialogController(
                this,
                scaler,
                new AuthInputFactory(this, scaler),
                new CredentialValidator(),
                dialogStyler,
                clickSoundBinder);
        loginDialogController.show(() -> {
            authGate.unlock();
            introVideoController.play(new VideoCompletionListener() {
                @Override
                public void onVideoFinished(String videoName) {
                    Toast.makeText(MainActivity.this, "已进入", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onVideoUnavailable(String videoName) {
                    Toast.makeText(MainActivity.this, "请替换 " + videoName, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void bindActions() {
        PanelSession session = new PanelSession();
        PanelApiClient apiClient = new PanelApiClient(session, new PanelSslConfigurator(), new JsonBodyFactory());
        PanelActionRunner runner = new PanelActionRunner(executorService, apiClient, authGate);
        PanelStatusPresenter presenter = new PanelStatusPresenter(this, mainHandler, homeStateController);
        DockActionBinder binder = new DockActionBinder(this, runner, presenter, authGate, clickSoundBinder);

        binder.bind(R.id.actionConnect, new PanelEndpoint("连接", "/login", true));
        binder.bind(R.id.actionOverview, new PanelEndpoint("概览", "/api/overview", false));
        binder.bind(R.id.actionTasks, new PanelEndpoint("任务", "/api/tasks", false));
        binder.bind(R.id.actionServices, new PanelEndpoint("服务", "/api/services", false));
        binder.bind(R.id.actionFiles, new PanelEndpoint("文件", "/api/files", false));
        binder.bind(R.id.actionLogs, new PanelEndpoint("日志", "/api/logs", false));

        android.view.View rootAction = findViewById(R.id.actionRoot);
        if (rootAction != null) {
            clickSoundBinder.bind(rootAction);
            rootAction.setOnClickListener(view -> new RootManagerDialogController(
                    this,
                    new DensityScaler(this),
                    new InfoLineFactory(this, new DensityScaler(this)),
                    new DialogStyler(new DensityScaler(this), profile),
                    new RootSecurityScanner(new RootCommandExecutor()),
                    executorService,
                    clickSoundBinder).show());
        }

        android.view.View settingsAction = findViewById(R.id.actionSettings);
        if (settingsAction != null) {
            clickSoundBinder.bind(settingsAction);
            settingsAction.setOnClickListener(view -> settingsDialogController.show(
                    introVideoController.getCurrentVideoName(),
                    () -> introVideoController.play(new VideoCompletionListener() {
                        @Override
                        public void onVideoFinished(String videoName) {
                            Toast.makeText(MainActivity.this, "重播完成", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onVideoUnavailable(String videoName) {
                            Toast.makeText(MainActivity.this, "请替换 " + videoName, Toast.LENGTH_SHORT).show();
                        }
                    })));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (introVideoController != null) {
            introVideoController.stop();
        }
        if (clickSoundBinder != null) {
            clickSoundBinder.release();
        }
        executorService.shutdownNow();
    }
}
