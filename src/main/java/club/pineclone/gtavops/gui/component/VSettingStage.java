package club.pineclone.gtavops.gui.component;

import club.pineclone.gtavops.gui.forked.ForkedSlider;
import club.pineclone.gtavops.gui.theme.ExtendedFontUsages;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.stage.VStage;
import io.vproxy.vfx.ui.stage.VStageInitParams;
import io.vproxy.vfx.ui.toggle.ToggleSwitch;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class VSettingStage {

    private final VStage vStage;
    private final VBox content;

    public VSettingStage() {
        vStage = new VStage(new VStageInitParams()
                .setMaximizeAndResetButton(false)
                .setIconifyButton(false)
        );

        vStage.getInitialScene().enableAutoContentWidthHeight();
        vStage.getStage().setWidth(800);
        vStage.getStage().setHeight(500);
        vStage.getStage().initModality(Modality.APPLICATION_MODAL);
        content = new VBox();
    }

    private void setTitle(String title) {

    }

    protected VBox getContent() {
        return content;
    }

    public VStage getStage() {
        return vStage;
    }

    public String getTitle() {
        return "";
    }

    public void show() {
        FXUtils.observeWidth(getStage().getInitialScene().getNode(), getContent(), -30);
        HBox hbox = new HBox(new HPadding(10), content);
        vStage.getInitialScene().getScrollPane().setContent(hbox);
        vStage.show();
    }

    public void showAndWait() {
        FXUtils.observeWidth(getStage().getInitialScene().getNode(), getContent(), -30);
        HBox hbox = new HBox(new HPadding(10), content);
        vStage.getInitialScene().getScrollPane().setContent(hbox);
        vStage.showAndWait();
    }

    protected void init() {}

    protected void stop() {}

    public void doInit() {
        vStage.setTitle(getTitle());
        init();
    }

    public void doStop() {
        stop();
    }

    protected HBox creatToggle(String intro, ToggleSwitch toggle) {
        HBox hBox = getBaseConfigContent(new Insets(22, 7, 0, 20));
        hBox.setPrefHeight(60);
        Region spacer = getSpacer();

        ThemeLabel label = new ThemeLabel(intro);
//        label.setOnMouseClicked(e ->
//                toggle.setSelected(!toggle.isSelected())
//        );

        hBox.getChildren().addAll(label, spacer, toggle.getNode());
        return hBox;
    }

    protected HBox createButton(String intro, FusionButton... buttons) {
        HBox hBox = getBaseConfigContent(new Insets(24, 7, 0, 20));
        Region spacer = getSpacer();

        ThemeLabel label = new ThemeLabel(intro);
        hBox.getChildren().addAll(label, spacer);
        Arrays.stream(buttons).forEach(hBox.getChildren()::addAll);
        return hBox;
    }

    protected HBox createDivider(String intro) {
        HBox hBox = getBaseConfigContent(new Insets(0, 0, 0, 0));
        hBox.setStyle("-fx-border-color: transparent transparent lightblue transparent; " +
                "-fx-border-width: 0 0 1 0;");

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.BOTTOM_LEFT);
        vBox.setPrefHeight(70);

        Region spacer = getSpacer();
        ThemeLabel label = new ThemeLabel(intro);
        FontManager.get().setFont(ExtendedFontUsages.dividerText, label);
        label.setStyle("-fx-text-fill: lightblue");
        vBox.getChildren().add(label);

        hBox.getChildren().addAll(vBox, spacer);
        return hBox;
    }

    protected HBox createSlider(String intro, ForkedSlider slider) {
        HBox hBox = getBaseConfigContent(new Insets(24, 7, 0, 20));
        hBox.setPrefHeight(70);
        Region spacer = getSpacer();

        ThemeLabel label = new ThemeLabel(intro);
        hBox.getChildren().addAll(label, spacer, slider);
        return hBox;
    }

    protected HBox createButtonToggle(String intro, ToggleSwitch toggle, FusionButton... buttons) {
        HBox baseContent = getBaseConfigContent(new Insets(0, 0, 0, 0));
        baseContent.setPrefHeight(60);

        HBox labelContent = new HBox(10);
        labelContent.setPadding(new Insets(22, 7, 0, 20));
        Region spacer = getSpacer();
        ThemeLabel label = new ThemeLabel(intro);
        labelContent.getChildren().addAll(label);

        HBox componentContent = new HBox(20);
        componentContent.setPadding(new Insets(24, 7, 0, 20));

        componentContent.getChildren().add(new HBox(20) {{
            Arrays.stream(buttons).forEach(this.getChildren()::addAll);
        }});

        componentContent.getChildren().add(new HBox() {{
            setPadding(new Insets(3, 0, 0, 0));
            getChildren().add(toggle.getNode());
        }});

        baseContent.getChildren().addAll(labelContent, spacer, componentContent);
        return baseContent;
    }

    protected HBox createGap(double height) {
        HBox hBox = getBaseConfigContent(new Insets(0, 0, 0, 0));
        hBox.setPrefHeight(height);
        return hBox;
    }

    private Region getSpacer() {
        Region spacer = new Region();
        spacer.setMinWidth(10);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private HBox getBaseConfigContent(Insets insets) {
        HBox hBox = new HBox(10);
        hBox.setPadding(insets);
        hBox.setPrefWidth(750);
        return hBox;
    }

    protected ContentBuilder contentBuilder() {
        return new ContentBuilder();
    }

    protected class ContentBuilder {
        private final List<Node> items = new ArrayList<>();

        public ContentBuilder toggle(String intro, ToggleSwitch toggle) {
            items.add(creatToggle(intro, toggle));
            return this;
        }

        public ContentBuilder button(String intro, FusionButton... buttons) {
            items.add(createButton(intro, buttons));
            return this;
        }

        public ContentBuilder buttonToggle(String intro, ToggleSwitch toggle, FusionButton... buttons) {
            items.add(createButtonToggle(intro, toggle, buttons));
            return this;
        }

        public ContentBuilder slider(String intro, ForkedSlider slider) {
            items.add(createSlider(intro, slider));
            return this;
        }

        public ContentBuilder gap(double height) {
            items.add(createGap(height));
            return this;
        }

        public ContentBuilder gap() {
            items.add(createGap(40));
            return this;
        }

        public ContentBuilder divide(String intro) {
            items.add(createDivider(intro));
            return this;
        }

        public List<Node> build() {
            items.add(new VPadding(20));
            return items;
        }
    }
}
