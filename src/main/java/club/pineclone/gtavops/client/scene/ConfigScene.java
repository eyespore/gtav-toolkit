package club.pineclone.gtavops.client.scene;

import club.pineclone.gtavops.client.component.VOptionButton;
import club.pineclone.gtavops.client.theme.ExtendedFontUsages;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class ConfigScene extends SceneTemplate {

    public ConfigScene(ExtendedI18n i18n) {
        super(i18n);
        ExtendedI18n.ConfigScene cI18n = i18n.configScene;
        enableAutoContentWidthHeight();

        var headerLabel = new ThemeLabel(cI18n.header);
        FXUtils.observeWidthCenter(getContentPane(), headerLabel);
        headerLabel.setLayoutY(40);

        VBox content = new VBox();
        content.setLayoutY(60);

        HBox hBox = new HBox(10);
        hBox.setPadding(new Insets(24, 7, 0, 20));
        hBox.setPrefWidth(650);

        Region spacer = new Region();
        spacer.setMinWidth(20);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        ThemeLabel label = new ThemeLabel("测试");
        label.setPadding(new Insets(6, 0, 0, 0));

        VOptionButton<String> button = new VOptionButton<>(List.of(
                new VOptionButton.OptionItem<>("test1", "test1"),
                new VOptionButton.OptionItem<>("test2", "test2")
        ));

        button.optionProperty().set(new VOptionButton.OptionItem<>("test1", "test1"));
        hBox.getChildren().addAll(label, spacer, button);
//        hBox.setAlignment(Pos.CENTER);

        content.getChildren().addAll(createDivider("test"), hBox);
        FXUtils.observeWidthCenter(getContentPane(), content);

        getContentPane().getChildren().addAll(
                headerLabel,
                content
        );
    }

    @Override
    public String getTitle() {
        return i18n.configScene.title;
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
        hBox.setPrefWidth(650);
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
}
