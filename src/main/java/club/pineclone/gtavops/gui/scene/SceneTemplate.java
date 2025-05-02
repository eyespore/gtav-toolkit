package club.pineclone.gtavops.gui.scene;

import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.ui.scene.VSceneRole;

public abstract class SceneTemplate extends VScene {

    public SceneTemplate(VSceneRole role) {
        super(role);
    }

    public abstract String getTitle();
}
