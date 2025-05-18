package club.pineclone.gtavops.gui.scene;

import club.pineclone.gtavops.context.GTAVRuntimeContext;
import club.pineclone.gtavops.gui.component.VOptionalButton;
import club.pineclone.gtavops.gui.feature.FeatureTogglePane;
import club.pineclone.gtavops.gui.feature.FeatureRegistry;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.i18n.I18nHolder;
import club.pineclone.gtavops.utils.ColorUtils;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;

public class _02FeatureScene extends SceneTemplate {

    ExtendedI18n i18n;
    ExtendedI18n.Feature fI18n;

    private GTAVRuntimeContext context;

    public _02FeatureScene() {
        super(VSceneRole.MAIN);
        i18n = I18nHolder.get();
        fI18n = i18n.feature;
        context = GTAVRuntimeContext.getINSTANCE();

        enableAutoContentWidthHeight();

        ThemeLabel headerLabel = new ThemeLabel(fI18n.header);
        ThemeLabel GameVerLabel = new ThemeLabel(fI18n.gameVersion + ": ");
        FXUtils.observeWidthCenter(getContentPane(), headerLabel);
        headerLabel.setLayoutY(40);

        HBox gameVersionPane = new HBox(10);
        gameVersionPane.setPadding(new Insets(24, 0, 0, 0));

        /* 传承版 */
        FusionButton gameVersionChooseBtn = new VOptionalButton() {{
            setPrefWidth(100);
            setPrefHeight(35);
//            setOnAction(e -> selectGameHome());
            setDisableAnimation(true);

            addOptionalItem(i18n.legacy);
            addOptionalItem(i18n.enhanced);

            indexProperty().addListener((ob, old, now) -> {
                int gameVersion = indexProperty().get();
                context.setGameVersion(gameVersion);

                Color color;
                if (gameVersion == 0) {
                    /* 传承版 */
                    color = Color.web("green");
                } else {
                    color = Color.web("lightblue");
                }
                getTextNode().setStyle("-fx-text-fill: " + ColorUtils.formatAsHex(color) + ";");
            });

            indexProperty().set(context.getGameVersion());
        }};
//        gameVersionPane.getChildren().addAll(GameVerLabel, gameVersionChooseBtn, headerLabel);
        gameVersionPane.getChildren().addAll(headerLabel);
        gameVersionPane.setAlignment(Pos.CENTER);
        gameVersionPane.setLayoutY(10);
        FXUtils.observeWidthCenter(getContentPane(), gameVersionPane);


        GridPane gridPane = new GridPane();
        gridPane.setLayoutY(120);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        FXUtils.observeWidthCenter(getContentPane(), gridPane);

        List<FeatureTogglePane> panes = FeatureRegistry.getInstance().getRegistry();

        int hLimit = 4;
        int hIndex = 0;
        int vIndex = 0;
        for (FeatureTogglePane enhance : panes) {
            gridPane.add(enhance.getNode(), hIndex, vIndex);
            hIndex++;
            if (hIndex >= hLimit) {
                vIndex++;
                hIndex = 0;
            }
        }

        getContentPane().getChildren().addAll(
//                headerLabel,
                gameVersionPane,
                gridPane
        );
    }

    @Override
    public String getTitle() {
        return I18nHolder.get().feature.title;
    }

}
