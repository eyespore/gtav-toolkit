package club.pineclone.gtavops.gui.scene;

import club.pineclone.gtavops.gui.feature.FeatureTogglePane;
import club.pineclone.gtavops.gui.feature.FeatureTogglePaneRegistry;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.i18n.I18nHolder;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;

import java.util.Collection;

public class _02FeatureScene extends SceneTemplate {

    ExtendedI18n i18n;
    ExtendedI18n.Feature fI18n;

    public _02FeatureScene() {
        super(VSceneRole.MAIN);
        i18n = I18nHolder.get();
        fI18n = i18n.feature;

        enableAutoContentWidthHeight();

        ThemeLabel headerLabel = new ThemeLabel(fI18n.header);
        FXUtils.observeWidthCenter(getContentPane(), headerLabel);
        headerLabel.setLayoutY(40);

        HBox gameVersionPane = new HBox(10);
        gameVersionPane.setPadding(new Insets(24, 0, 0, 0));

        gameVersionPane.getChildren().addAll(headerLabel);
        gameVersionPane.setAlignment(Pos.CENTER);
        gameVersionPane.setLayoutY(10);
        FXUtils.observeWidthCenter(getContentPane(), gameVersionPane);


        GridPane gridPane = new GridPane();
        gridPane.setLayoutY(120);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        FXUtils.observeWidthCenter(getContentPane(), gridPane);

        Collection<FeatureTogglePane> panes = FeatureTogglePaneRegistry.getInstance().values();

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
