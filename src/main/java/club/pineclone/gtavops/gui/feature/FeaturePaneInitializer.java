package club.pineclone.gtavops.gui.feature;

import io.vproxy.vfx.control.globalscreen.GlobalScreenUtils;

import java.util.List;

public class FeaturePaneInitializer {

    private static final FeaturePaneInitializer instance = new FeaturePaneInitializer();

    public synchronized void enable() {
        GlobalScreenUtils.enable(this);  // todo: 将逻辑抽离到真正的MacroManager
        List<FeatureTogglePane> registry = FeatureRegistry.getInstance().getRegistry();
        registry.forEach(FeatureTogglePane::init);
    }

    public synchronized void disable() {
        List<FeatureTogglePane> registry = FeatureRegistry.getInstance().getRegistry();
        registry.forEach(FeatureTogglePane::stop);
        GlobalScreenUtils.disable(this);
    }

    public static FeaturePaneInitializer getInstance() {
        return instance;
    }

}
