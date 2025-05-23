package club.pineclone.gtavops.gui.feature;

import club.pineclone.gtavops.common.ResourceHolder;
import club.pineclone.gtavops.config.Config;
import club.pineclone.gtavops.gui.component.VKeyChooseButton;
import club.pineclone.gtavops.gui.component.VSettingStage;
import club.pineclone.gtavops.gui.forked.ForkedKeyChooser;
import club.pineclone.gtavops.gui.forked.ForkedSlider;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.macro.MacroContextHolder;
import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.action.impl.StartEngineAction;
import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerFactory;
import club.pineclone.gtavops.macro.trigger.TriggerIdentity;
import club.pineclone.gtavops.macro.trigger.TriggerMode;
import io.vproxy.vfx.entity.input.Key;

import java.util.UUID;

public class _05BetterMMenuFeatureTogglePane
        extends FeatureTogglePane
        implements ResourceHolder {

    public _05BetterMMenuFeatureTogglePane() {
        super(new BMMFeatureContext(), new BMMSettingStage());
    }

    @Override
    protected String getTitle() {
        return getI18n().betterMMenu.title;
    }

    @Override
    public boolean init() {
        return getConfig().betterMMenu.baseSetting.enable;
    }

    @Override
    public void stop(boolean enabled) {
        getConfig().betterMMenu.baseSetting.enable = enabled;
    }

    private static class BMMFeatureContext
            extends FeatureContext
            implements ResourceHolder, MacroContextHolder {

        private UUID macroId;
        private final Config config = getConfig();
        private final Config.BetterMMenu bmmConfig = config.betterMMenu;

        @Override
        protected void activate() {
            Key startEngineKey = bmmConfig.baseSetting.startEngineKey;
            Key menuKey = bmmConfig.baseSetting.menuKey;
            long arrowKeyInterval = (long) (Math.floor(bmmConfig.baseSetting.arrowKeyInterval));
            long enterKeyInterval = (long) (Math.floor(bmmConfig.baseSetting.enterKeyInterval));

            Trigger trigger = TriggerFactory.simple(new TriggerIdentity(TriggerMode.CLICK, startEngineKey));
            Action action = new StartEngineAction(menuKey, arrowKeyInterval, enterKeyInterval);

            macroId = MACRO_FACTORY.createSimpleMacro(trigger, action);
            MACRO_REGISTRY.install(macroId);
        }

        @Override
        protected void deactivate() {
            MACRO_REGISTRY.uninstall(macroId);
        }

    }

    private static class BMMSettingStage
            extends VSettingStage
            implements ResourceHolder {

        private final ExtendedI18n i18n = getI18n();
        private final ExtendedI18n.BetterMMenu bmmI18n = i18n.betterMMenu;

        private final Config config = getConfig();
        private final Config.BetterMMenu bmmConfig = config.betterMMenu;

        private static final int FLAG_WITH_KEY_AND_MOUSE = ForkedKeyChooser.FLAG_WITH_KEY  | ForkedKeyChooser.FLAG_WITH_MOUSE;
        private static final int FLAG_WITH_ALL = FLAG_WITH_KEY_AND_MOUSE | ForkedKeyChooser.FLAG_WITH_WHEEL_SCROLL;

        private final VKeyChooseButton menuKeyBtn = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);
        private final ForkedSlider arrowKeyIntervalSlider = new ForkedSlider() {{
            setLength(200);
            setRange(1, 50);
        }};
        private final ForkedSlider enterKeyIntervalSlider = new ForkedSlider() {{
            setLength(200);
            setRange(1, 50);
        }};
        private final VKeyChooseButton startEngineKeyBtn = new VKeyChooseButton(FLAG_WITH_ALL);

        public BMMSettingStage() {
            getContent().getChildren().addAll(contentBuilder()
                    .divide(bmmI18n.baseSetting.title)
                    .button(bmmI18n.baseSetting.menuKey, menuKeyBtn)
                    .slider(bmmI18n.baseSetting.arrowKeyInterval, arrowKeyIntervalSlider)
                    .slider(bmmI18n.baseSetting.enterKeyInterval, enterKeyIntervalSlider)
                    .button(bmmI18n.baseSetting.startEngineKey, startEngineKeyBtn)
                    .build());
        }

        @Override
        public String getTitle() {
            return bmmI18n.title;
        }

        @Override
        public void init() {
            menuKeyBtn.keyProperty().set(bmmConfig.baseSetting.menuKey);
            arrowKeyIntervalSlider.setValue(bmmConfig.baseSetting.arrowKeyInterval);
            enterKeyIntervalSlider.setValue(bmmConfig.baseSetting.enterKeyInterval);
            startEngineKeyBtn.keyProperty().set(bmmConfig.baseSetting.startEngineKey);
        }

        @Override
        public void stop() {
            bmmConfig.baseSetting.menuKey = menuKeyBtn.keyProperty().get();
            bmmConfig.baseSetting.arrowKeyInterval = arrowKeyIntervalSlider.valueProperty().get();
            bmmConfig.baseSetting.enterKeyInterval = enterKeyIntervalSlider.valueProperty().get();
            bmmConfig.baseSetting.startEngineKey = startEngineKeyBtn.keyProperty().get();
        }
    }
}
