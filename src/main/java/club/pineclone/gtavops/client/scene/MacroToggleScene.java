package club.pineclone.gtavops.client.scene;

import club.pineclone.gtavops.client.macrotoggle.MacroToggle;
import club.pineclone.gtavops.client.macrotoggle.*;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;

import java.util.List;

public class MacroToggleScene extends SceneTemplate {

    private final List<MacroToggle> macroToggles;

    public MacroToggleScene(ExtendedI18n i18n) {
        super(i18n);
        ExtendedI18n.FeatureScene fI18n = i18n.featureScene;

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

        int hLimit = 4;
        int hIndex = 0;
        int vIndex = 0;

        /* 注册所有宏功能开关 */
        macroToggles = List.of(
                new SwapGlitchToggle(i18n),
                new RouletteSnakeToggle(i18n),
                new ADSwingToggle(i18n),
                new MeleeGlitchToggle(i18n),
                new BetterMMenuToggle(i18n),
                new BetterLButtonToggle(i18n),
                new QuickSwapToggle(i18n),
                new DelayClimbToggle(i18n),
                new BetterPMenuToggle(i18n),
                new AutoFireFeatureToggle(i18n)
        );

        for (MacroToggle toggle : macroToggles) {
            gridPane.add(toggle.getNode(), hIndex, vIndex);
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
        return i18n.featureScene.title;
    }

    @Override
    public void onUIInit() {
//        FeatureTogglePaneRegistry.getInstance().initAll();  /* 初始化所有的特性项 */
        macroToggles.forEach(MacroToggle::onUIInit);
    }

    @Override
    public void onUIDispose() {
//        FeatureTogglePaneRegistry.getInstance().stopAll();  /* 停止所有的特性项 */
        macroToggles.forEach(MacroToggle::onUIDispose);
    }
}
