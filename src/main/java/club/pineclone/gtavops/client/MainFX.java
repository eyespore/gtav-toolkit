package club.pineclone.gtavops.client;

import club.pineclone.gtavops.config.MacroConfigLoader;
import club.pineclone.gtavops.config.MacroConfig;
import club.pineclone.gtavops.client.scene.*;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.i18n.I18nHolder;
import club.pineclone.gtavops.utils.ImageUtils;
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
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

/* 主窗口 */
public class MainFX extends Application {

    public static final String APPLICATION_TITLE = "GTAV Ops";  /* 应用基础信息 */
    private static final Set<FXLifecycleAware> fxListeners = new HashSet<>();
    private static final Set<UILifecycleAware> uiListeners = new HashSet<>();

    private final List<SceneTemplate> mainScenes = new ArrayList<>();
    private VSceneGroup sceneGroup;

    private FusionPane navigatePane;
    private List<FusionButton> navigatorButtons;
    private boolean isSwitch = false;

    @Override
    public void start(Stage primaryStage) throws Exception {
        for (FXLifecycleAware fxListener : fxListeners) {
            fxListener.onFXStart();
        }

        ExtendedI18n i18n = I18nHolder.get();  /* 本地化在 FXInit 阶段完成初始化 */
        MacroConfig config = MacroConfigLoader.get();  /* 配置文件在 FXInit 阶段完成初始化 */

        ExtendedI18n.IntroScene iI18n = i18n.introScene;

        VStage vStage = new VStage(new VStageInitParams().setResizable(false).setMaximizeAndResetButton(false)) {
            @Override
            public void close() {
                super.close();
                TaskManager.get().terminate();
                GlobalScreenUtils.unregister();
            }
        };
        vStage.getInitialScene().enableAutoContentWidthHeight();
        vStage.setTitle(APPLICATION_TITLE);

        /* 注册所有场景 */
        mainScenes.add(new IntroScene(i18n));
        mainScenes.add(new MacroToggleScene(i18n));
        mainScenes.add(new ConfigScene(i18n));
//        mainScenes.add(new FontPackScene(i18n));
        mainScenes.forEach(MainFX::addUIListener);

        /* 调用初始化逻辑 */
        for (UILifecycleAware uiListener : uiListeners) {
            uiListener.onUIInit();
        }

        var initialScene = mainScenes.get(0);

        /* 场景导航栏 */
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
            add(createNavigateButton(iI18n.configNavigate, 2));
//            add(createNavigateButton(iI18n.fontpackNavigate, 3));
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
        menuScene.getNode().setPrefWidth(300);
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
            final var targetIndex = i;
            var scene = mainScenes.get(i);
            var title = scene.getTitle();
            var button = new FusionButton(title);
            button.setDisableAnimation(true);
            button.setOnAction(e -> {
                //noinspection SuspiciousMethodCalls
                var currentIndex = mainScenes.indexOf(sceneGroup.getCurrentMainScene());
                if (currentIndex != targetIndex) {
                    sceneGroup.show(scene, currentIndex < targetIndex ? VSceneShowMethod.FROM_RIGHT : VSceneShowMethod.FROM_LEFT);

                    /* 修复通过左侧菜单栏切换页面导航按钮不变化的问题 */
                    for (int naviBtnIndex = 0; naviBtnIndex < navigatorButtons.size(); naviBtnIndex++) {
                        navigatorButtons.get(naviBtnIndex).setDisable(naviBtnIndex == targetIndex);
                    }
                }
                vStage.getRootSceneGroup().hide(menuScene, VSceneHideMethod.TO_LEFT);
//                prevButton.setDisable(targetIndex == 0);
//                nextButton.setDisable(targetIndex == mainScenes.size() - 1);
            });
            button.setPrefWidth(250);
            button.setPrefHeight(40);
            if (i != 0) {
                menuVBox.getChildren().add(new VPadding(20));
            }
            menuVBox.getChildren().add(button);
        }
        menuVBox.getChildren().add(new VPadding(20));

        var menuBtn = new FusionImageButton(ImageUtils.loadImage("/img/check.png")) {{
            setPrefWidth(40);
            setPrefHeight(VStage.TITLE_BAR_HEIGHT + 1);
            getImageView().setFitHeight(15);
            setLayoutX(-2);
            setLayoutY(-1);
        }};

//        menuBtn.setOnAction(e -> vStage.getRootSceneGroup().show(menuScene, VSceneShowMethod.FROM_LEFT));
        menuBtn.setOnAction(e -> {});

        vStage.getRoot().getContentPane().getChildren().add(menuBtn);

        vStage.getStage().setWidth(1000);
        vStage.getStage().setHeight(600);
        vStage.getStage().getIcons().add(ImageUtils.loadImage("/img/favicon.png"));
        vStage.getStage().centerOnScreen();
        vStage.getStage().show();
    }

    public static void addFXListener(FXLifecycleAware listener) {
        fxListeners.add(listener);
    }

    public static void addUIListener(UILifecycleAware listener) {
        uiListeners.add(listener);
    }

    private FusionButton createNavigateButton(String text, int index) {
        return new FusionButton(text) {{
            setPrefWidth(150);
            setPrefHeight(navigatePane.getNode().getPrefHeight() - FusionPane.PADDING_V * 2);  /* 40 */
            setDisableAnimation(true);

            setOnAction(e -> {
                if (isSwitch) return;  /* Scene 正在切换，禁用事件点击 */

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
        /* 显式注册监听器 */
        // 添加所有注册的场景
        for (FXLifecycleAware fxListener : fxListeners) {
            fxListener.onFXInit();
        }
    }

    @Override
    public void stop() throws Exception {
        /* 调用销毁逻辑，需要先调用UI销毁逻辑，再调用生命周期销毁逻辑 */
        for (UILifecycleAware uiListener : uiListeners) {
            uiListener.onUIDispose();
        }

        for (FXLifecycleAware fxListener : fxListeners) {
            fxListener.onFXStop();
        }
    }
}
