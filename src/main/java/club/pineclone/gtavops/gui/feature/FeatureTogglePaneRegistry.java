package club.pineclone.gtavops.gui.feature;

import club.pineclone.gtavops.common.AbstractRegistry;
import io.vproxy.vfx.control.globalscreen.GlobalScreenUtils;
import lombok.Getter;

import java.util.UUID;

public class FeatureTogglePaneRegistry extends AbstractRegistry<FeatureTogglePane> {

    @Getter
    private static final FeatureTogglePaneRegistry instance = new FeatureTogglePaneRegistry();

    private FeatureTogglePaneRegistry() {
        register(new _01SwapGlitchFeatureTogglePane());
        register(new _02QuickSnakeFeatureTogglePane());
        register(new _03ADSwingFeatureTogglePane());
        register(new _04MeleeGlitchFeatureTogglePane());
        register(new _05BetterMMenuFeatureTogglePane());
        register(new _06BetterLButtonFeatureTogglePane());
        register(new _07QuickSwapFeatureTogglePane());
    }

    public synchronized void initAll() {
        GlobalScreenUtils.enable(this);
        values().forEach(FeatureTogglePane::doInit);
    }

    public synchronized void stopAll() {
        values().forEach(FeatureTogglePane::doStop);
        GlobalScreenUtils.disable(this);
    }

}
