package club.pineclone.gtavops.client.macrotoggle;

import club.pineclone.gtavops.common.ResourceHolder;
import club.pineclone.gtavops.config.MacroConfig;
import club.pineclone.gtavops.config.MacroConfigLoader;
import club.pineclone.gtavops.client.component.VKeyChooseButton;
import club.pineclone.gtavops.client.forked.ForkedKeyChooser;
import club.pineclone.gtavops.client.forked.ForkedSlider;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.macro.MacroCreationStrategies;
import club.pineclone.gtavops.macro.MacroRegistry;
import io.vproxy.vfx.ui.toggle.ToggleSwitch;

import java.util.UUID;

public class BetterMMenuToggle extends MacroToggle {

    private UUID startEngineMacroId;
    private UUID autoSnakeMacroId;

    public BetterMMenuToggle(ExtendedI18n i18n) {
        super(i18n);
    }

    @Override
    protected void onFeatureEnable() {
        MacroConfig.BetterMMenu bmmConfig = MacroConfigLoader.get().betterMMenu;
        /* 快速点火宏启用 */
        if (bmmConfig.startEngine.enable) {
            startEngineMacroId = MacroRegistry.getInstance().register(MacroConfigLoader.get(), MacroCreationStrategies.START_ENGINE_MACRO_CREATION_STRATEGY);
            MacroRegistry.getInstance().launchMacro(startEngineMacroId);
        }
        /* 自动零食宏启用 */
        if (bmmConfig.autoSnake.enable) {
            autoSnakeMacroId = MacroRegistry.getInstance().register(MacroConfigLoader.get(), MacroCreationStrategies.AUTO_SNAKE_MACRO_CREATION_STRATEGY);
            MacroRegistry.getInstance().launchMacro(autoSnakeMacroId);
        }
    }

    @Override
    protected void onFeatureDisable() {
        MacroRegistry.getInstance().terminateMacro(startEngineMacroId);
        MacroRegistry.getInstance().terminateMacro(autoSnakeMacroId);
    }

    @Override
    protected String getTitle() {
        return i18n.betterMMenu.title;
    }

    @Override
    protected MacroSettingStage getSetting() {
        return new BMMSettingStage(i18n);
    }

    @Override
    public void onUIInit() {
        selectedProperty().set(MacroConfigLoader.get().betterMMenu.baseSetting.enable);
    }

    @Override
    public void onUIDispose() {
        MacroConfigLoader.get().betterMMenu.baseSetting.enable = selectedProperty().get();
    }

    private static class BMMSettingStage
            extends MacroSettingStage
            implements ResourceHolder {

        private final ExtendedI18n.BetterMMenu bmmI18n = i18n.betterMMenu;

        private final MacroConfig config = getConfig();
        private final MacroConfig.BetterMMenu bmmConfig = config.betterMMenu;

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
            setLength(250);
            setRange(10, 2000);
        }};

        /* start engine */
        private final ToggleSwitch enableStartEngineToggle = new ToggleSwitch();
        private final VKeyChooseButton startEngineActivateKeyBtn = new VKeyChooseButton(FLAG_WITH_ALL);
        private final ToggleSwitch enableDoubleClickToOpenDoorToggle = new ToggleSwitch();
        private final ForkedSlider doubleClickIntervalSlider = new ForkedSlider() {{
            setLength(200);
            setRange(50, 500);
        }};

        /* auto snake */
        private final ToggleSwitch enableAutoSnakeToggle = new ToggleSwitch();
        private final VKeyChooseButton autoSnakeActivateKeyBtn = new VKeyChooseButton(FLAG_WITH_KEY_AND_MOUSE);
        private final ToggleSwitch autoSnakeKeepMMenuToggle = new ToggleSwitch();
        private final ToggleSwitch autoSnakeRefillVestToggle = new ToggleSwitch();

        public BMMSettingStage(ExtendedI18n i18n) {
            super(i18n);
            getContent().getChildren().addAll(contentBuilder()
                    .divide(bmmI18n.baseSetting.title)
                    .button(bmmI18n.baseSetting.menuKey, menuKeyBtn)
                    .slider(bmmI18n.baseSetting.mouseScrollInterval, arrowKeyIntervalSlider)
                    .slider(bmmI18n.baseSetting.keyPressInterval, enterKeyIntervalSlider)
                    .slider(bmmI18n.baseSetting.timeUtilMMenuLoaded, timeUtilMMenuLoadedSlider)
                    .divide(bmmI18n.startEngine.title)
                    .toggle(bmmI18n.startEngine.enableStartEngine, enableStartEngineToggle)
                    .button(bmmI18n.startEngine.activateKey, startEngineActivateKeyBtn)
                    .toggle(bmmI18n.startEngine.enableDoubleClickToOpenDoor, enableDoubleClickToOpenDoorToggle)
                    .slider(bmmI18n.startEngine.doubleClickInterval, doubleClickIntervalSlider)
                    .divide(bmmI18n.autoSnake.title)
                            .toggle(bmmI18n.autoSnake.enableAutoSnake, enableAutoSnakeToggle)
                            .button(bmmI18n.autoSnake.activateKey, autoSnakeActivateKeyBtn)
                            .toggle(bmmI18n.autoSnake.refillVest, autoSnakeRefillVestToggle)
                            .toggle(bmmI18n.autoSnake.keepMMenu, autoSnakeKeepMMenuToggle)
                    .build());
        }

        @Override
        public String getTitle() {
            return bmmI18n.title;
        }

        @Override
        public void onVSettingStageInit() {
            /* base settings */
            menuKeyBtn.keyProperty().set(bmmConfig.baseSetting.menuKey);
            arrowKeyIntervalSlider.setValue(bmmConfig.baseSetting.mouseScrollInterval);
            enterKeyIntervalSlider.setValue(bmmConfig.baseSetting.keyPressInterval);
            timeUtilMMenuLoadedSlider.setValue(bmmConfig.baseSetting.timeUtilMMenuLoaded);

            /* start engine */
            enableStartEngineToggle.selectedProperty().set(bmmConfig.startEngine.enable);
            startEngineActivateKeyBtn.keyProperty().set(bmmConfig.startEngine.activateKey);
            enableDoubleClickToOpenDoorToggle.selectedProperty().set(bmmConfig.startEngine.enableDoubleClickToOpenDoor);
            doubleClickIntervalSlider.setValue(bmmConfig.startEngine.doubleClickInterval);

            /* auto snake */
            enableAutoSnakeToggle.selectedProperty().set(bmmConfig.autoSnake.enable);
            autoSnakeActivateKeyBtn.keyProperty().set(bmmConfig.autoSnake.activateKey);
            autoSnakeRefillVestToggle.selectedProperty().set(bmmConfig.autoSnake.refillVest);
            autoSnakeKeepMMenuToggle.selectedProperty().set(bmmConfig.autoSnake.keepMMenu);
        }

        @Override
        public void onVSettingStageExit() {
            /* base settings */
            bmmConfig.baseSetting.menuKey = menuKeyBtn.keyProperty().get();
            bmmConfig.baseSetting.mouseScrollInterval = arrowKeyIntervalSlider.valueProperty().get();
            bmmConfig.baseSetting.keyPressInterval = enterKeyIntervalSlider.valueProperty().get();
            bmmConfig.baseSetting.timeUtilMMenuLoaded = timeUtilMMenuLoadedSlider.valueProperty().get();

            /* start engine */
            bmmConfig.startEngine.activateKey = startEngineActivateKeyBtn.keyProperty().get();
            bmmConfig.startEngine.enable = enableStartEngineToggle.selectedProperty().get();
            bmmConfig.startEngine.enableDoubleClickToOpenDoor = enableDoubleClickToOpenDoorToggle.selectedProperty().get();
            bmmConfig.startEngine.doubleClickInterval = doubleClickIntervalSlider.valueProperty().get();

            /* auto snake */
            bmmConfig.autoSnake.enable = enableAutoSnakeToggle.selectedProperty().get();
            bmmConfig.autoSnake.activateKey = autoSnakeActivateKeyBtn.keyProperty().get();
            bmmConfig.autoSnake.refillVest = autoSnakeRefillVestToggle.selectedProperty().get();
            bmmConfig.autoSnake.keepMMenu = autoSnakeKeepMMenuToggle.selectedProperty().get();
        }
    }
}
