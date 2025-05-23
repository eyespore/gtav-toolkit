package club.pineclone.gtavops.gui.feature;

import club.pineclone.gtavops.gui.component.VSettingStage;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.toggle.ToggleSwitch;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public abstract class FeatureTogglePane {

    private final ToggleSwitch toggle;  /* 激活开关 */
    private final FusionPane content;  /* 面板 */

    protected final FeatureContext context;  /* 功能上下文 */
    protected final VSettingStage setting;

    public FeatureTogglePane(final FeatureContext context, final VSettingStage setting) {
        this.context = context;
        this.setting = setting;

        Region spacer = new Region();
        spacer.setMinWidth(10);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        toggle = new ToggleSwitch();

        ThemeLabel intro = new ThemeLabel(getTitle());
        HBox hBox = new HBox(0);
        hBox.setPadding(new Insets(12, 22, 0, 5));
        hBox.getChildren().addAll(intro, spacer, toggle.getNode());
        hBox.setPrefWidth(260);

        FusionPane pane = new FusionPane();
        pane.getNode().setPrefWidth(260);
        pane.getNode().setPrefHeight(75);
        pane.getContentPane().getChildren().add(hBox);

        ClickEventHandler handler = new ClickEventHandler(toggle);
        pane.getNode().setOnMouseClicked(handler);
        toggle.getNode().setOnMouseClicked(handler);
        toggle.selectedProperty().addListener(new ToggleEventHandler());

        this.content = pane;
    }

    private class ToggleEventHandler implements ChangeListener<Boolean> {
        @Override
        public void changed(ObservableValue<? extends Boolean> obs, Boolean oldV, Boolean newV) {
            if (newV) context.activate();
            else context.deactivate();
        }
    }

    private class ClickEventHandler implements EventHandler<MouseEvent> {
        private final ToggleSwitch toggle;

        public ClickEventHandler(ToggleSwitch toggle) {
            this.toggle = toggle;
        }

        @Override
        public void handle(MouseEvent e) {
            if (e.getButton() == MouseButton.PRIMARY) {
                boolean flag = toggle.selectedProperty().get();  // 判断启用宏
                toggle.setSelected(!flag);

            } else if (e.getButton() == MouseButton.SECONDARY) {
                VSettingStage settingStage = FeatureTogglePane.this.setting;
                if (settingStage == null) return;

                FeatureTogglePaneRegistry.getInstance().stopAll();  // 禁用宏，启用设置
                settingStage.doInit();  // 设置页面初始化
                settingStage.showAndWait();  // 展示设置页面
                settingStage.doStop();
                FeatureTogglePaneRegistry.getInstance().initAll();  // 重新启用宏
            }
        }
    }

    public Node getNode() {
        return content.getNode();
    }

    private BooleanProperty selectedProperty() {
        return toggle.selectedProperty();
    }

    protected abstract String getTitle();

    /**
     * 功能上下文初始化，{@link FeatureTogglePaneRegistry}会在应用初始化阶段对{@link FeatureTogglePaneRegistry}
     * 注册表当中注册的所有宏功能类进行遍历，逐个调用此方法，进行初始化和激活
     *
     * @return 初始化之后，此功能面板是否应该处于开启状态
     */
    public abstract boolean init();

    /**
     * 功能上下文停止，{@link FeatureTogglePaneRegistry}会在停止阶段对注册表当中注册的功能逐个调用此方法，以在用户进行功能设置、应用退出
     * 之前正确的关闭宏功能，避免一些问题出现
     *
     * @param enabled 停止时此功能面板是否处于开启状态，用于结合配置持久化开启状态
     */
    public abstract void stop(boolean enabled);


    protected final void doInit() {
        selectedProperty().set(init());
    }

    protected final void doStop() {
        stop(selectedProperty().get());
        selectedProperty().set(false);
    }
}
