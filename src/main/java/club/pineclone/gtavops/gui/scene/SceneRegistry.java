package club.pineclone.gtavops.gui.scene;

import club.pineclone.gtavops.utils.AbstractRegistry;
import lombok.Getter;

public class SceneRegistry extends AbstractRegistry<SceneTemplate> {

    @Getter
    private static final SceneRegistry instance = new SceneRegistry();

    private SceneRegistry() {
        registry.add(new _01IntroScene());
        registry.add(new _02FeatureScene());
        registry.add(new _03FontPackScene());
        registry.add(new _04ConfigScene());
    }
}
