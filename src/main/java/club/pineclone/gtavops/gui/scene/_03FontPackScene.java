package club.pineclone.gtavops.gui.scene;

import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.i18n.I18nHolder;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;

public class _03FontPackScene extends SceneTemplate {

    ExtendedI18n i18n;
    ExtendedI18n.FontPack fpI18n;

    public _03FontPackScene() {
        super(VSceneRole.MAIN);
        i18n = I18nHolder.get();
        fpI18n = i18n.fontPack;
        enableAutoContentWidthHeight();

        var msgLabel = new ThemeLabel(fpI18n.header);
        FXUtils.observeWidthCenter(getContentPane(), msgLabel);
        msgLabel.setLayoutY(100);

        getContentPane().getChildren().addAll(
                msgLabel
        );
    }

    @Override
    public String getTitle() {
        return I18nHolder.get().fontPack.title;
    }
}
