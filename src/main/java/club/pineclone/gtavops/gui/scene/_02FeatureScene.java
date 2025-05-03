package club.pineclone.gtavops.gui.scene;

import club.pineclone.gtavops.gui.feature.FeatureTogglePane;
import club.pineclone.gtavops.gui.feature.FeatureRegistry;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.i18n.I18nHolder;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import javafx.scene.layout.*;

import java.util.List;

public class _02FeatureScene extends SceneTemplate {

    ExtendedI18n i18n;
    ExtendedI18n.Feature fI18n;

    public _02FeatureScene() {
        super(VSceneRole.MAIN);
        i18n = I18nHolder.get();
        fI18n = i18n.feature;

        enableAutoContentWidthHeight();

        var msgLabel = new ThemeLabel(fI18n.header);
        FXUtils.observeWidthCenter(getContentPane(), msgLabel);
        msgLabel.setLayoutY(100);

        GridPane gridPane = new GridPane();
        gridPane.setLayoutY(200);
        gridPane.setHgap(50);
        gridPane.setVgap(50);
        FXUtils.observeWidthCenter(getContentPane(), gridPane);

        List<FeatureTogglePane> panes = FeatureRegistry.getInstance().getRegistry();

        int hLimit = 3;
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
                msgLabel,
                gridPane
        );
    }

    @Override
    public String getTitle() {
        return I18nHolder.get().feature.title;
    }

}
