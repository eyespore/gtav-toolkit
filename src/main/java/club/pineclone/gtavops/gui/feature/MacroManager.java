package club.pineclone.gtavops.gui.feature;

import io.vproxy.vfx.control.globalscreen.GlobalScreenUtils;

import java.util.List;

public class MacroManager {

    private static final MacroManager instance = new MacroManager();

    public synchronized void enable() {
        GlobalScreenUtils.enable(this);
        List<FeatureTogglePane> registry = MarcoRegistry.getInstance().getRegistry();
        registry.forEach(FeatureTogglePane::init);
    }

    public synchronized void disable() {
        List<FeatureTogglePane> registry = MarcoRegistry.getInstance().getRegistry();
        registry.forEach(FeatureTogglePane::stop);
        GlobalScreenUtils.disable(this);
    }

    public static MacroManager getInstance() {
        return instance;
    }

}
