package club.pineclone.gtavops;

import club.pineclone.gtavops.config.ConfigHolder;
import club.pineclone.gtavops.gui.feature.FeaturePaneInitializer;
import club.pineclone.gtavops.gui.forked.ForkedAlert;
import club.pineclone.gtavops.gui.forked.ForkedDialog;
import club.pineclone.gtavops.gui.forked.ForkedDialogButton;
import club.pineclone.gtavops.gui.scene.SceneRegistry;
import club.pineclone.gtavops.gui.scene.SceneTemplate;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.i18n.I18nHolder;
import club.pineclone.gtavops.macro.action.ActionTaskManager;
import club.pineclone.gtavops.utils.ImageUtils;
import club.pineclone.gtavops.utils.JLibLocator;
import club.pineclone.gtavops.utils.PathUtils;
import club.pineclone.gtavops.utils.SingletonLock;
import com.github.kwhat.jnativehook.GlobalScreen;
import io.vproxy.base.util.LogType;
import io.vproxy.vfx.control.globalscreen.GlobalScreenUtils;
import io.vproxy.vfx.manager.task.TaskManager;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.button.FusionImageButton;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.scene.*;
import io.vproxy.vfx.ui.stage.VStage;
import io.vproxy.vfx.ui.stage.VStageInitParams;
import io.vproxy.vfx.util.FXUtils;
import io.vproxy.vpacket.dns.rdata.A;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainFX extends Application {

    private final List<SceneTemplate> mainScenes = new ArrayList<>();
    private VSceneGroup sceneGroup;

    private Exception configLoadException = null;  /* 配置加载异常 */
    private Exception appHomeInitException = null;  /* 应用家目录初始化异常 */
    private Exception i18nInitException = null;
    private Exception duplicatedAppInstanceException = null;

    private FusionPane navigatePane;
    private List<FusionButton> navigatorButtons;

    private void handleConfigLoadException() {
        ExtendedI18n i18n = I18nHolder.get();
        /* 存在配置文件加载错误 */
        if (configLoadException != null) {
            ForkedDialog<Integer> dialog = ForkedDialog.stackTraceDialog(i18n.configFileLoadFailed, configLoadException);
            Optional<Integer> result = dialog.showAndWait();
            if (result.filter(i -> i == ForkedDialog.CONFIRM).isPresent()) {
                try {  /* 尝试重载配置文件 */
                    ConfigHolder.overrideConfigToDefault();
                } catch (IOException e2) {
                    ForkedDialog.stackTraceDialog(i18n.configStillLoadFailed, e2, ForkedDialog.CONFIRM).showAndWait();
                    System.exit(1);  /* 第二次抛出异常则退出程序 */
                }
            } else {  /* 用户取消加载配置文件 */
                System.exit(1);
            }
        }
    }

    private void handleI18nInitException() {
        /* 存在配置文件加载错误 */
        if (i18nInitException != null) {
            ForkedDialog<Integer> dialog = ForkedDialog.stackTraceDialog(i18nInitException, ForkedDialog.CONFIRM);
            dialog.showAndWait();
            System.exit(1);  /* 本地化文件加载错误直接退出 */
        }
    }

    private void handleAppHomeInitException() {
        if (appHomeInitException != null) {
            ForkedDialog<Integer> dialog = ForkedDialog.stackTraceDialog(appHomeInitException, ForkedDialog.CONFIRM);
            dialog.showAndWait();
            System.exit(1);  /* 家目录初始化失败直接退出 */
        }
    }

    private void handleSingletonLockAcquireException() {
        if (duplicatedAppInstanceException != null) {
            ForkedDialog<Integer> dialog = ForkedDialog.stackTraceDialog(duplicatedAppInstanceException, ForkedDialog.CONFIRM);
            dialog.showAndWait();
            System.exit(1);  /* 家目录初始化失败直接退出 */
        }
    }

    @Override
    public void start(Stage primaryStage) throws ClassNotFoundException {
        handleI18nInitException();  /* 处理本地化加载失败 */
        handleSingletonLockAcquireException();  /* 处理单例失败 */
        handleAppHomeInitException();  /* 处理家目录初始化失败 */
        handleConfigLoadException();  /* 处理配置文件错误 */

        ExtendedI18n i18n = I18nHolder.get();
        ExtendedI18n.Intro iI18n = i18n.intro;

        Class.forName(ActionTaskManager.class.getName());  /* 任务调度 */
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());  /* 停止jnativehook日志记录 */
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
        FeaturePaneInitializer.getInstance().enable();  /* 启用宏引擎 */

        VStage vStage = new VStage(primaryStage) {
            @Override
            public void close() {
                super.close();
                TaskManager.get().terminate();
                GlobalScreenUtils.unregister();
            }
        };
        vStage.getInitialScene().enableAutoContentWidthHeight();
        vStage.setTitle(ConfigHolder.APPLICATION_TITLE);

        mainScenes.addAll(SceneRegistry.getInstance().getRegistry());  // 添加所有注册的场景

        // var initialScene = mainScenes.stream().filter(e -> e instanceof ).findAny().get();
        var initialScene = mainScenes.get(0);
        sceneGroup = new VSceneGroup(initialScene);
        for (var s : mainScenes) {
            if (s == initialScene) continue;
            sceneGroup.addScene(s);
        }

        navigatePane = new FusionPane();
        navigatePane.getNode().setPrefHeight(60);
        FXUtils.observeHeight(vStage.getInitialScene().getContentPane(), sceneGroup.getNode(), -10 - 60 - 5 - 10);
        FXUtils.observeWidth(vStage.getInitialScene().getContentPane(), sceneGroup.getNode(), -20);
        FXUtils.observeWidth(vStage.getInitialScene().getContentPane(), navigatePane.getNode(), -20);

        navigatorButtons = new ArrayList<>() {{
            add(createNavigateButton(iI18n.introNavigate, 0));
            add(createNavigateButton(iI18n.featureNavigate, 1));
            add(createNavigateButton(iI18n.fontpackNavigate, 2));
        }};

        navigatorButtons.get(0).setDisable(true);  /* 默认页禁用 */
        navigatePane.getContentPane().getChildren().addAll(navigatorButtons);
        navigatePane.getContentPane().widthProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            double totalWidth = 0;
            double spacing = FusionPane.PADDING_H;

            // 计算总宽度：按钮总宽度 + 间隔总宽度
            for (FusionButton btn : navigatorButtons) {
                totalWidth += btn.getPrefWidth();
            }
            totalWidth += spacing * (navigatorButtons.size() - 1);

            double x = now.doubleValue() / 2 - totalWidth / 2;

            for (FusionButton btn : navigatorButtons) {
                btn.setLayoutX(x);
                x += btn.getPrefWidth() + spacing;
            }
        });

        var box = new HBox(
                new HPadding(10),
                new VBox(
                        new VPadding(10),
                        sceneGroup.getNode(),
                        new VPadding(5),
                        navigatePane.getNode()
                )
        );
        vStage.getInitialScene().getContentPane().getChildren().add(box);

        var menuScene = new VScene(VSceneRole.DRAWER_VERTICAL);
        menuScene.getNode().setPrefWidth(450);
        menuScene.enableAutoContentWidth();
        menuScene.getNode().setBackground(new Background(new BackgroundFill(
                Theme.current().subSceneBackgroundColor(),
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));

        vStage.getRootSceneGroup().addScene(menuScene, VSceneHideMethod.TO_LEFT);
        var menuVBox = new VBox() {{
            setPadding(new Insets(0, 0, 0, 24));
            getChildren().add(new VPadding(20));
        }};
        menuScene.getContentPane().getChildren().add(menuVBox);
        for (int i = 0; i < mainScenes.size(); ++i) {
            final var fi = i;
            var scene = mainScenes.get(i);
            var title = scene.getTitle();
            var button = new FusionButton(title);
            button.setDisableAnimation(true);
            button.setOnAction(e -> {
                //noinspection SuspiciousMethodCalls
                var currentIndex = mainScenes.indexOf(sceneGroup.getCurrentMainScene());
                if (currentIndex != fi) {
                    sceneGroup.show(scene, currentIndex < fi ? VSceneShowMethod.FROM_RIGHT : VSceneShowMethod.FROM_LEFT);
                }
                vStage.getRootSceneGroup().hide(menuScene, VSceneHideMethod.TO_LEFT);
//                prevButton.setDisable(fi == 0);
//                nextButton.setDisable(fi == mainScenes.size() - 1);
            });
            button.setPrefWidth(400);
            button.setPrefHeight(40);
            if (i != 0) {
                menuVBox.getChildren().add(new VPadding(20));
            }
            menuVBox.getChildren().add(button);
        }
        menuVBox.getChildren().add(new VPadding(20));

        var menuBtn = new FusionImageButton(ImageUtils.loadImage("/img/menu.png")) {{
            setPrefWidth(40);
            setPrefHeight(VStage.TITLE_BAR_HEIGHT + 1);
            getImageView().setFitHeight(15);
            setLayoutX(-2);
            setLayoutY(-1);
        }};
        menuBtn.setOnAction(e -> vStage.getRootSceneGroup().show(menuScene, VSceneShowMethod.FROM_LEFT));
        vStage.getRoot().getContentPane().getChildren().add(menuBtn);

        vStage.getStage().setWidth(1200);
        vStage.getStage().setHeight(700);
        vStage.getStage().getIcons().add(ImageUtils.loadImage("/img/favicon.png"));
        vStage.getStage().centerOnScreen();
        vStage.getStage().show();
    }

    private boolean isSwitch = false;

    private FusionButton createNavigateButton(String text, int index) {
        return new FusionButton(text) {{
            setPrefWidth(150);
            setPrefHeight(navigatePane.getNode().getPrefHeight() - FusionPane.PADDING_V * 2);  /* 40 */
            setDisableAnimation(true);

            setOnAction(e -> {
                if (isSwitch) return;
                var current = sceneGroup.getCurrentMainScene();
                //noinspection SuspiciousMethodCalls
                int currentIndex = mainScenes.indexOf(current);
                if (currentIndex == index) return;

                VSceneShowMethod method = index > currentIndex ?
                        VSceneShowMethod.FROM_RIGHT : VSceneShowMethod.FROM_LEFT;

                isSwitch = true;
                navigatorButtons.forEach(b -> b.setDisable(true));

                SceneTemplate newScene = mainScenes.get(index);
                sceneGroup.show(newScene, method);

                PauseTransition pause = new PauseTransition(Duration.millis(400));
                pause.setOnFinished(e2 -> {
                    isSwitch = false;
                    for (int i = 0; i < navigatorButtons.size(); i++) {
                        navigatorButtons.get(i).setDisable(i == index);
                    }
                });
                pause.play();
            });
        }};
    }

    @Override
    public void init() throws Exception {
        try {
            I18nHolder.load();  /* 初始化本地化 */
        } catch (IOException e) {
            i18nInitException = e;  /* i18n文件错误 */
            return;
        }

        /* 获取单例锁 */
        boolean flag = SingletonLock.lockInstance();
        if (!flag) {
            /* 程序已经在运行 */
            duplicatedAppInstanceException = new RuntimeException(I18nHolder.get().duplicatedAppInstanceRunning);
            return;
        }

        Class.forName(JLibLocator.class.getName());  /* 加载本地库 */

        try {
            PathUtils.initAppHome();
        } catch (IOException e) {
            appHomeInitException = e;  /* 应用家目录初始化异常 */
            return;
        }

        try {
            ConfigHolder.load();
        } catch (IOException e) {
            configLoadException = e;  /* 配置初始化异常 */
            return;
        }
    }

    @Override
    public void stop() throws Exception {
        FeaturePaneInitializer.getInstance().disable();  /* 停止宏引擎 */
        ActionTaskManager.shutdown();  /* 停止任务调度 */
        ConfigHolder.save();  /* 保存配置 */
    }

}
