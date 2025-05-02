package club.pineclone.gtavops.gui.component;

import club.pineclone.gtavops.gui.forked.ForkedSlider;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.stage.VStage;
import io.vproxy.vfx.ui.stage.VStageInitParams;
import io.vproxy.vfx.ui.toggle.ToggleSwitch;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

public abstract class SettingStage {

    private final VStage vStage;
    private final VBox content;

    public SettingStage() {
        vStage = new VStage(new VStageInitParams()
                .setMaximizeAndResetButton(false)
                .setIconifyButton(false)
        );

        vStage.getInitialScene().enableAutoContentWidthHeight();
        vStage.getStage().setWidth(800);
        vStage.getStage().setHeight(400);
        vStage.getStage().initModality(Modality.APPLICATION_MODAL);
        vStage.setTitle(getTitle());
        content = new VBox();
    }

    protected VBox getContent() {
        return content;
    }

    public VStage getStage() {
        return vStage;
    }

    public void show() {
        HBox hbox = new HBox(new HPadding(10), content);
        vStage.getInitialScene().getScrollPane().setContent(hbox);
        vStage.show();
    }

    public void showAndWait() {
        HBox hbox = new HBox(new HPadding(10), content);
        vStage.getInitialScene().getScrollPane().setContent(hbox);
        vStage.showAndWait();
    }

    public String getTitle() {
        return "";
    }

    public void init() {}

    public void stop() {}

    protected HBox creatToggle(String intro, ToggleSwitch toggle) {
        HBox hBox = getBaseConfigContent(new Insets(12, 7, 0, 20));
        Region spacer = getSpacer();

        ThemeLabel label = new ThemeLabel(intro);
//        label.setOnMouseClicked(e ->
//                toggle.setSelected(!toggle.isSelected())
//        );

        hBox.getChildren().addAll(label, spacer, toggle.getNode());
        return hBox;
    }

    protected HBox createButton(String intro, FusionButton button) {
        HBox hBox = getBaseConfigContent(new Insets(24, 7, 0, 20));
        Region spacer = getSpacer();

        ThemeLabel label = new ThemeLabel(intro);
        hBox.getChildren().addAll(label, spacer, button);
        return hBox;
    }

    protected HBox createSlider(String intro, ForkedSlider slider) {
        HBox hBox = getBaseConfigContent(new Insets(24, 7, 0, 20));
        Region spacer = getSpacer();

        ThemeLabel label = new ThemeLabel(intro);
        hBox.getChildren().addAll(label, spacer, slider);
        return hBox;
    }

    private Region getSpacer() {
        Region spacer = new Region();
        spacer.setMinWidth(10);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private HBox getBaseConfigContent(Insets insets) {
        HBox hBox = new HBox(0);
        hBox.setPadding(insets);
        hBox.setPrefWidth(750);
        return hBox;
    }
}
