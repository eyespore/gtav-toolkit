package club.pineclone.gtavops.gui.feature;

import club.pineclone.gtavops.gui.component.SettingStage;
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

    public FeatureTogglePane() {
        Region spacer = new Region();
        spacer.setMinWidth(10);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        toggle = new ToggleSwitch();
        ThemeLabel intro = new ThemeLabel(getTitle());

        HBox hBox = new HBox(0);
        hBox.setPadding(new Insets(12, 22, 0, 5));
        hBox.getChildren().addAll(intro, spacer, toggle.getNode());
        hBox.setPrefWidth(300);

        FusionPane pane = new FusionPane();
        pane.getNode().setPrefWidth(300);
        pane.getNode().setPrefHeight(75);
        pane.getContentPane().getChildren().add(hBox);

        ClickEventHandler handler = new ClickEventHandler(toggle);
        pane.getNode().setOnMouseClicked(handler);
        toggle.getNode().setOnMouseClicked(handler);
        toggle.selectedProperty().addListener(new ToggleEventHandler());

        this.content = pane;
    }

    /**
     * 开关状态监听器，监听此功能状态开关，并在状态转变到激活时调用{@link FeatureTogglePane#activate()}方法以启用
     * 功能，当状态转变到关闭时调用{@link FeatureTogglePane#deactivate()}方法以停止功能
     */
    private class ToggleEventHandler implements ChangeListener<Boolean> {
        @Override
        public void changed(ObservableValue<? extends Boolean> obs, Boolean oldV, Boolean newV) {
            if (newV) activate();
            else deactivate();
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
                SettingStage settingStage = FeatureTogglePane.this.getSettingStage();
                if (settingStage == null) return;

                FeaturePaneInitializer.getInstance().disable();  // 禁用宏，启用设置
                settingStage.init();  // 设置页面初始化
                settingStage.showAndWait();  // 展示设置页面
                settingStage.stop();
                FeaturePaneInitializer.getInstance().enable();  // 重新启用宏
            }
        }
    }

    public Node getNode() {
        return content.getNode();
    }

    public BooleanProperty selectedProperty() {
        return toggle.selectedProperty();
    }

    protected abstract String getTitle();

    /**
     * 应该避免直接调用这个方法启动功能
     * 激活功能
     */
    protected abstract void activate();

    /**
     * 外部不应该调用此方法停止宏功能
     * 停止功能
     */
    protected abstract void deactivate();

    /**
     * 功能上下文初始化，{@link FeaturePaneInitializer}会在应用初始化阶段对{@link FeatureRegistry}
     * 注册表当中注册的所有宏功能类进行遍历，逐个调用此方法，进行初始化和激活
     *
     */
    public abstract void init();

    /**
     * 功能上下文停止，{@link FeaturePaneInitializer}会在停止阶段对注册表当中注册的功能逐个调用此方法，以在用户进行功能设置、应用退出
     * 之前正确的关闭宏功能，避免一些问题出现
     */
    public abstract void stop();

    public SettingStage getSettingStage() {
        return null;
    }
}
