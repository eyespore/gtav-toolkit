package club.pineclone.gtavops.gui.feature;

import club.pineclone.gtavops.utils.AbstractRegistry;
import lombok.Getter;

public class FeatureRegistry extends AbstractRegistry<FeatureTogglePane> {

    @Getter
    private static final FeatureRegistry instance = new FeatureRegistry();

    static {
        instance.registry.add(new _01SwapGlitchFeatureTogglePane());
        instance.registry.add(new _02QuickSnakeFeatureTogglePane());
        instance.registry.add(new _03ADSwingFeatureTogglePane());
        instance.registry.add(new _04MeleeGlitchFeatureTogglePane());
        instance.registry.add(new _05BetterMMenuFeatureTogglePane());
    }

    private FeatureRegistry() {}

}
