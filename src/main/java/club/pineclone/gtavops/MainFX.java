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
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

    @Override
    public void start(Stage primaryStage) throws ClassNotFoundException {
        if (configLoadException != null) {
            var dialog = new ForkedDialog<Integer>(new VStage(
                    new VStageInitParams().setIconifyButton(false).setMaximizeAndResetButton(false)
            ));
            ExtendedI18n i18n = I18nHolder.get();

            dialog.setText(MessageFormat.format(i18n.configFileLoadFailed, configLoadException.getMessage()));
            dialog.setButtons(Arrays.asList(
                    new ForkedDialogButton<>(i18n.confirm, 1),
                    new ForkedDialogButton<>(i18n.cancel, 0)
            ));

            dialog.getvStage().getStage().initModality(Modality.APPLICATION_MODAL);
            Optional<Integer> result = dialog.showAndWait();

            if (result.isPresent() && result.get() == 1) {
                /* 重载配置文件 */
                try {
                    ConfigHolder.overrideConfigToDefault();
                } catch (IOException e2) {
                    ForkedAlert.showAndWait(Alert.AlertType.WARNING,
                            MessageFormat.format(i18n.configStillLoadFailed, e2.getMessage()));
                    io.vproxy.base.util.Logger.info(LogType.ALERT, "However program throws another exception");
                    System.exit(1);  /* 第二次抛出异常则退出程序 */
                }
            } else {
                io.vproxy.base.util.Logger.info(LogType.ALERT, "user cancel config overriding");
                System.exit(1);
            }
        }

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

        var navigatePane = new FusionPane();

        navigatePane.getNode().setPrefHeight(60);
        FXUtils.observeHeight(vStage.getInitialScene().getContentPane(), sceneGroup.getNode(), -10 - 60 - 5 - 10);

        FXUtils.observeWidth(vStage.getInitialScene().getContentPane(), sceneGroup.getNode(), -20);
        FXUtils.observeWidth(vStage.getInitialScene().getContentPane(), navigatePane.getNode(), -20);

        var prevButton = new FusionButton("<< Previous") {{
            setPrefWidth(150);
            setPrefHeight(navigatePane.getNode().getPrefHeight() - FusionPane.PADDING_V * 2);
            setOnlyAnimateWhenNotClicked(true);

            var current = sceneGroup.getCurrentMainScene();
            //noinspection SuspiciousMethodCalls
            var index = mainScenes.indexOf(current);
            if (index == 0) {
                setDisable(true);
            }
        }};
        var nextButton = new FusionButton("Next >>") {{
            setPrefWidth(150);
            setPrefHeight(navigatePane.getNode().getPrefHeight() - FusionPane.PADDING_V * 2);
            setOnlyAnimateWhenNotClicked(true);

            var current = sceneGroup.getCurrentMainScene();
            //noinspection SuspiciousMethodCalls
            var index = mainScenes.indexOf(current);
            if (index == mainScenes.size() - 1) {
                setDisable(true);
            }
        }};
        prevButton.setOnAction(e -> {
            var current = sceneGroup.getCurrentMainScene();
            //noinspection SuspiciousMethodCalls
            var index = mainScenes.indexOf(current);
            if (index == 0) return;
            sceneGroup.show(mainScenes.get(index - 1), VSceneShowMethod.FROM_LEFT);
            if (index - 1 == 0) {
                prevButton.setDisable(true);
            }
            nextButton.setDisable(false);
        });
        nextButton.setOnAction(e -> {
            var current = sceneGroup.getCurrentMainScene();
            //noinspection SuspiciousMethodCalls
            var index = mainScenes.indexOf(current);
            if (index == mainScenes.size() - 1) return;
            sceneGroup.show(mainScenes.get(index + 1), VSceneShowMethod.FROM_RIGHT);
            if (index + 1 == mainScenes.size() - 1) {
                nextButton.setDisable(true);
            }
            prevButton.setDisable(false);
        });

        navigatePane.getContentPane().getChildren().add(prevButton);
        navigatePane.getContentPane().getChildren().add(nextButton);
        navigatePane.getContentPane().widthProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            var v = now.doubleValue();
            nextButton.setLayoutX(v - nextButton.getPrefWidth());
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
            var s = mainScenes.get(i);
            var title = s.getTitle();
            var button = new FusionButton(title);
            button.setDisableAnimation(true);
            button.setOnAction(e -> {
                //noinspection SuspiciousMethodCalls
                var currentIndex = mainScenes.indexOf(sceneGroup.getCurrentMainScene());
                if (currentIndex != fi) {
                    sceneGroup.show(s, currentIndex < fi ? VSceneShowMethod.FROM_RIGHT : VSceneShowMethod.FROM_LEFT);
                }
                vStage.getRootSceneGroup().hide(menuScene, VSceneHideMethod.TO_LEFT);
                prevButton.setDisable(fi == 0);
                nextButton.setDisable(fi == mainScenes.size() - 1);
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

        vStage.getStage().setWidth(1300);
        vStage.getStage().setHeight(820);
        vStage.getStage().getIcons().add(ImageUtils.loadImage("/img/favicon.png"));
        vStage.getStage().centerOnScreen();
        vStage.getStage().show();
    }

    @Override
    public void init() throws Exception {
        Class.forName(JLibLocator.class.getName());  /* 本地库 */

        try {
            I18nHolder.load();  /* 初始化本地化 */
        } catch (IOException e) {
            io.vproxy.base.util.Logger.error(LogType.SYS_ERROR, "i18n file load filed");
            throw e;
        }

        try {
            ConfigHolder.load();
        } catch (IOException e) {
            /* 配置初始化异常 */
            configLoadException = e;
            io.vproxy.base.util.Logger.error(LogType.SYS_ERROR, "config file load failed");
        }
    }

    @Override
    public void stop() throws Exception {
        FeaturePaneInitializer.getInstance().disable();  /* 停止宏引擎 */
        ActionTaskManager.shutdown();  /* 停止任务调度 */
        ConfigHolder.save();  /* 保存配置 */
    }

}
