package club.pineclone.gtavops.gui.scene;

import club.pineclone.gtavops.common.AbstractRegistry;
import lombok.Getter;

public class SceneRegistry extends AbstractRegistry<SceneTemplate> {

    @Getter private static final SceneRegistry instance = new SceneRegistry();

    private SceneRegistry() {
        register(new _01IntroScene());
        register(new _02FeatureScene());
        register(new _03FontPackScene());
        register(new _04ConfigScene());
    }
}
