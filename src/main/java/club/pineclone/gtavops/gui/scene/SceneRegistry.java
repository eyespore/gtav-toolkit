package club.pineclone.gtavops.gui.scene;

import club.pineclone.gtavops.utils.AbstractRegistry;

public class SceneRegistry extends AbstractRegistry<SceneTemplate> {

    private static final SceneRegistry instance = new SceneRegistry();

    private SceneRegistry() {
        registry.add(new _01IntroScene());
        registry.add(new _02FeatureScene());
        registry.add(new _03SettingScene());
    }

    public static SceneRegistry getInstance() {
        return instance;
    }
}
