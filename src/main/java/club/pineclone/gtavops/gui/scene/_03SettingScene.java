package club.pineclone.gtavops.gui.scene;

import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.i18n.I18nHolder;
import io.vproxy.commons.util.Singleton;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;

public class _03SettingScene extends SceneTemplate {

    public _03SettingScene() {
        super(VSceneRole.MAIN);
        enableAutoContentWidthHeight();

        var msgLabel = new ThemeLabel(I18nHolder.get().settingSceneHeader);
        FXUtils.observeWidthCenter(getContentPane(), msgLabel);
        msgLabel.setLayoutY(100);

        getContentPane().getChildren().addAll(
                msgLabel
        );
    }

    @Override
    public String getTitle() {
        return I18nHolder.get().settingSceneTitle;
    }
}
