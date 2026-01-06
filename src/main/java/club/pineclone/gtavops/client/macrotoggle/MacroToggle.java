package club.pineclone.gtavops.client.macrotoggle;

import club.pineclone.gtavops.client.UILifecycleAware;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.toggle.ToggleSwitch;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import javafx.beans.property.BooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 功能开关用于配置某一项功能是否被开启，主要用于编写宏相关功能的UI界面，一个功能开关在左键点击时会开关对应的功能，
 * 右键点击时会打开一个配置界面
 */
public abstract class MacroToggle implements UILifecycleAware {

    private final ToggleSwitch toggle;  /* 宏功能开关 */
    private final FusionPane content;  /* UI 面板 */

    protected final ExtendedI18n i18n;
    protected final Logger log = LoggerFactory.getLogger(getClass());

    public MacroToggle(ExtendedI18n i18n) {
        this.i18n = i18n;

        Region spacer = new Region();
        spacer.setMinWidth(10);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        toggle = new ToggleSwitch();

        ThemeLabel intro = new ThemeLabel(getTitle());
        intro.setPadding(new Insets(4, 0, 0, 0));

        HBox hBox = new HBox(0);
        hBox.setPadding(new Insets(6, 22, 0, 5));
        hBox.getChildren().addAll(intro, spacer, toggle.getNode());
        hBox.setPrefWidth(230);

        FusionPane pane = new FusionPane();
        pane.getNode().setPrefWidth(230);
        pane.getNode().setPrefHeight(65);
        pane.getContentPane().getChildren().add(hBox);

        ClickEventHandler handler = new ClickEventHandler(toggle);
        pane.getNode().setOnMouseClicked(handler);
        toggle.getNode().setOnMouseClicked(handler);

        toggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) onFeatureEnable();
            else onFeatureDisable();
        });

        this.content = pane;
    }

    /**
     * 当功能开关被启用时，该方法会被调用
     */
    protected abstract void onFeatureEnable();

    /**
     * 当功能开关被关闭时，该方法会被调用
     */
    protected abstract void onFeatureDisable();

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
            } else if (e.getButton() == MouseButton.SECONDARY) {  /* 右击弹出设置菜单 */
                MacroSettingStage settingStage = getSetting();
                if (settingStage == null) return;  /* 设置菜单不存在，直接返回 */
                settingStage.initVSettingStage();  // 设置页面初始化
                settingStage.showAndWait();  // 展示设置页面
                settingStage.exitVSettingStage();
            }
        }
    }

    public Node getNode() {
        return content.getNode();
    }

    protected BooleanProperty selectedProperty() {
        return toggle.selectedProperty();
    }

    protected abstract String getTitle();

    protected abstract MacroSettingStage getSetting();
}
