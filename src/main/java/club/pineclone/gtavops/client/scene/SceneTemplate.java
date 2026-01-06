package club.pineclone.gtavops.client.scene;

import club.pineclone.gtavops.client.UILifecycleAware;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.ui.scene.VSceneRole;

public abstract class SceneTemplate extends VScene implements UILifecycleAware {

    protected final ExtendedI18n i18n;

    public SceneTemplate(ExtendedI18n i18n) {
        super(VSceneRole.MAIN);
        this.i18n = i18n;
    }

    public abstract String getTitle();

    // TODO: 改用FXLifeCycleAware注入生命周期事件，实现从Scene到MainFX的逆向回调，完善切换Scene时的逻辑


}
