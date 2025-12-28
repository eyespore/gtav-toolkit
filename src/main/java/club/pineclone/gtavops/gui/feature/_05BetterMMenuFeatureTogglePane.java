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
import club.pineclone.gtavops.macro.action.impl.bettermmenu.StartEngineAction;
import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerFactory;
import club.pineclone.gtavops.macro.trigger.TriggerIdentity;
import club.pineclone.gtavops.macro.trigger.TriggerMode;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.ui.toggle.ToggleSwitch;

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
            long mouseScrollInterval = (long) (Math.floor(bmmConfig.baseSetting.mouseScrollInterval));
            long enterKeyInterval = (long) (Math.floor(bmmConfig.baseSetting.enterKeyInterval));
            long timeUtilMMenuLoaded = (long) (Math.floor(bmmConfig.baseSetting.timeUtilMMenuLoaded));
            Key menuKey = bmmConfig.baseSetting.menuKey;

            /* 快速点火 */
            if (bmmConfig.startEngine.enable) {
                Key startEngineKey = bmmConfig.startEngine.startEngineKey;

                boolean enableDoubleClickToOpenDoor = bmmConfig.startEngine.enableDoubleClickToOpenDoor;
                long doubleClickInterval = (long) (Math.floor(bmmConfig.startEngine.doubleClickInterval));

                Trigger trigger;
                if (enableDoubleClickToOpenDoor) trigger = TriggerFactory.simple(TriggerIdentity.ofDoubleClick(doubleClickInterval, startEngineKey));  // 启用双击触发
                else trigger = TriggerFactory.simple(TriggerIdentity.ofClick(startEngineKey));  // 仅启用单击触发

                Action action = new StartEngineAction(menuKey, mouseScrollInterval, enterKeyInterval, timeUtilMMenuLoaded, enableDoubleClickToOpenDoor);

                macroId = MACRO_FACTORY.createSimpleMacro(trigger, action);
                MACRO_REGISTRY.install(macroId);
            }
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
            setRange(10, 100);
        }};
        private final ForkedSlider enterKeyIntervalSlider = new ForkedSlider() {{
            setLength(200);
            setRange(10, 100);
        }};

        private final ForkedSlider timeUtilMMenuLoadedSlider = new ForkedSlider() {{
            setLength(400);
            setRange(10, 2000);
        }};

        /* start engine */
        private final VKeyChooseButton startEngineKeyBtn = new VKeyChooseButton(FLAG_WITH_ALL);
        private final ToggleSwitch enableStartEngineToggle = new ToggleSwitch();
        private final ToggleSwitch enableDoubleClickToOpenDoorToggle = new ToggleSwitch();
        private final ForkedSlider doubleClickIntervalSlider = new ForkedSlider() {{
            setLength(200);
            setRange(50, 500);
        }};

        public BMMSettingStage() {
            getContent().getChildren().addAll(contentBuilder()
                    .divide(bmmI18n.baseSetting.title)
                    .button(bmmI18n.baseSetting.menuKey, menuKeyBtn)
                    .slider(bmmI18n.baseSetting.mouseScrollInterval, arrowKeyIntervalSlider)
                    .slider(bmmI18n.baseSetting.enterKeyInterval, enterKeyIntervalSlider)
                    .slider(bmmI18n.baseSetting.timeUtilMMenuLoaded, timeUtilMMenuLoadedSlider)
                    .divide(bmmI18n.startEngine.title)
                    .toggle(bmmI18n.startEngine.enableStartEngine, enableStartEngineToggle)
                    .button(bmmI18n.startEngine.startEngineKey, startEngineKeyBtn)
                    .toggle(bmmI18n.startEngine.enableDoubleClickToOpenDoor, enableDoubleClickToOpenDoorToggle)
                    .slider(bmmI18n.startEngine.doubleClickInterval, doubleClickIntervalSlider)
                    .build());
        }

        @Override
        public String getTitle() {
            return bmmI18n.title;
        }

        @Override
        public void onVSettingStageInit() {
            menuKeyBtn.keyProperty().set(bmmConfig.baseSetting.menuKey);
            arrowKeyIntervalSlider.setValue(bmmConfig.baseSetting.mouseScrollInterval);
            enterKeyIntervalSlider.setValue(bmmConfig.baseSetting.enterKeyInterval);
            timeUtilMMenuLoadedSlider.setValue(bmmConfig.baseSetting.timeUtilMMenuLoaded);

            startEngineKeyBtn.keyProperty().set(bmmConfig.startEngine.startEngineKey);
            enableStartEngineToggle.selectedProperty().set(bmmConfig.startEngine.enable);
            enableDoubleClickToOpenDoorToggle.selectedProperty().set(bmmConfig.startEngine.enableDoubleClickToOpenDoor);
            doubleClickIntervalSlider.setValue(bmmConfig.startEngine.doubleClickInterval);

        }

        @Override
        public void onVSettingStageExit() {
            bmmConfig.baseSetting.menuKey = menuKeyBtn.keyProperty().get();
            bmmConfig.baseSetting.mouseScrollInterval = arrowKeyIntervalSlider.valueProperty().get();
            bmmConfig.baseSetting.enterKeyInterval = enterKeyIntervalSlider.valueProperty().get();
            bmmConfig.baseSetting.timeUtilMMenuLoaded = timeUtilMMenuLoadedSlider.valueProperty().get();

            bmmConfig.startEngine.startEngineKey = startEngineKeyBtn.keyProperty().get();
            bmmConfig.startEngine.enable = enableStartEngineToggle.selectedProperty().get();
            bmmConfig.startEngine.enableDoubleClickToOpenDoor = enableDoubleClickToOpenDoorToggle.selectedProperty().get();
            bmmConfig.startEngine.doubleClickInterval = doubleClickIntervalSlider.valueProperty().get();
        }
    }
}
