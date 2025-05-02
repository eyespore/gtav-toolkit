package club.pineclone.gtavops.gui.feature;

import club.pineclone.gtavops.utils.AbstractRegistry;
import lombok.Getter;

public class MarcoRegistry extends AbstractRegistry<FeatureTogglePane> {

    @Getter
    private static final MarcoRegistry instance = new MarcoRegistry();

    static {
        instance.registry.add(new _01SwapGlitchFeatureTogglePane());
        instance.registry.add(new _02RestoreStrengthenFeatureTogglePane());
    }

    private MarcoRegistry() {}

}
