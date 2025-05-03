package club.pineclone.gtavops.gui.scene;

import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.i18n.I18nHolder;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;

public class _04ConfigScene extends SceneTemplate {

    ExtendedI18n i18n;
    ExtendedI18n.Config cI18n;

    public _04ConfigScene() {
        super(VSceneRole.MAIN);
        i18n = I18nHolder.get();
        cI18n = i18n.config;
        enableAutoContentWidthHeight();

        var msgLabel = new ThemeLabel(cI18n.header);
        FXUtils.observeWidthCenter(getContentPane(), msgLabel);
        msgLabel.setLayoutY(100);

        getContentPane().getChildren().addAll(
                msgLabel
        );
    }

    @Override
    public String getTitle() {
        return I18nHolder.get().config.title;
    }
}
