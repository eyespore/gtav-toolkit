package club.pineclone.gtavops.client.macrotoggle;

import club.pineclone.gtavops.common.ResourceHolder;
import club.pineclone.gtavops.config.MacroConfig;
import club.pineclone.gtavops.config.MacroConfigLoader;
import club.pineclone.gtavops.client.component.VTriggerModeChooseButton;
import club.pineclone.gtavops.client.component.VKeyChooseButton;
import club.pineclone.gtavops.client.forked.ForkedKeyChooser;
import club.pineclone.gtavops.client.forked.ForkedSlider;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.macro.MacroCreationStrategies;
import club.pineclone.gtavops.macro.MacroRegistry;
import io.vproxy.vfx.ui.toggle.ToggleSwitch;

import java.util.UUID;

public class BetterLButtonToggle extends MacroToggle {

    private UUID holdLButtonMacroId;
    private UUID rapidlyClickLButtonMacroId;
    private UUID remapLButtonMacroId;

    public BetterLButtonToggle(ExtendedI18n i18n) {
        super(i18n);
    }

    @Override
    protected void onFeatureEnable() {
        MacroConfig.BetterLButton blbConfig = MacroConfigLoader.get().betterLButton;
        /* 辅助按住鼠标左键 */
        if (blbConfig.holdLButtonSetting.enable) {
            holdLButtonMacroId = MacroRegistry.getInstance().register(MacroConfigLoader.get(), MacroCreationStrategies.HOLD_LEFT_BUTTON_MACRO_CREATION_STRATEGY);
            MacroRegistry.getInstance().launchMacro(holdLButtonMacroId);
        }
        /* 辅助点按鼠标左键 */
        if (blbConfig.rapidlyClickLButtonSetting.enable) {
            rapidlyClickLButtonMacroId = MacroRegistry.getInstance().register(MacroConfigLoader.get(), MacroCreationStrategies.RAPIDLY_CLICK_LEFT_BUTTON_MACRO_CREATION_STRATEGY);
            MacroRegistry.getInstance().launchMacro(rapidlyClickLButtonMacroId);
        }
        /* 鼠标左键重映射 */
        if (blbConfig.remapLButtonSetting.enable) {
            remapLButtonMacroId = MacroRegistry.getInstance().register(MacroConfigLoader.get(), MacroCreationStrategies.RAPIDLY_CLICK_LEFT_BUTTON_MACRO_CREATION_STRATEGY);
            MacroRegistry.getInstance().launchMacro(remapLButtonMacroId);
        }
    }

    @Override
    protected void onFeatureDisable() {
        MacroRegistry.getInstance().terminateMacro(holdLButtonMacroId);
        MacroRegistry.getInstance().terminateMacro(rapidlyClickLButtonMacroId);
        MacroRegistry.getInstance().terminateMacro(remapLButtonMacroId);
    }

    @Override
    protected String getTitle() {
        return i18n.betterLButton.title;
    }

    @Override
    protected MacroSettingStage getSetting() {
        return new BLBSettingStage(i18n);
    }

    @Override
    public void onUIInit() {
        selectedProperty().set(MacroConfigLoader.get().betterLButton.baseSetting.enable);
    }

    @Override
    public void onUIDispose() {
        MacroConfigLoader.get().betterLButton.baseSetting.enable = selectedProperty().get();
    }

    private static class BLBSettingStage
            extends MacroSettingStage
            implements ResourceHolder {

        private final ExtendedI18n.BetterLButton blbI18n = i18n.betterLButton;

        private final MacroConfig config = getConfig();
        private final MacroConfig.BetterLButton blbConfig = config.betterLButton;

        private static final int FLAG_WITH_KEY_AND_MOUSE = ForkedKeyChooser.FLAG_WITH_KEY  | ForkedKeyChooser.FLAG_WITH_MOUSE;
        private static final int FLAG_WITH_ALL = FLAG_WITH_KEY_AND_MOUSE | ForkedKeyChooser.FLAG_WITH_WHEEL_SCROLL;

        private final ToggleSwitch enableHoldLButtonToggle = new ToggleSwitch();
        private final VKeyChooseButton holdLButtonActivateKeyBtn = new VKeyChooseButton(FLAG_WITH_ALL);
        private final VTriggerModeChooseButton holdLButtonActivateMethodBtn = new VTriggerModeChooseButton(
                VTriggerModeChooseButton.FLAG_WITH_HOLD | VTriggerModeChooseButton.FLAG_WITH_TOGGLE);

        private final ToggleSwitch enableRapidlyClickLButtonToggle = new ToggleSwitch();
        private final VKeyChooseButton rapidlyClickLButtonActivateKeyBtn = new VKeyChooseButton(FLAG_WITH_ALL);
        private final VTriggerModeChooseButton rapidlyClickLButtonActivateMethodBtn = new VTriggerModeChooseButton(
                VTriggerModeChooseButton.FLAG_WITH_HOLD | VTriggerModeChooseButton.FLAG_WITH_TOGGLE);
        private final ForkedSlider rapidlyClickLButtonTriggerInterval = new ForkedSlider() {{
            setLength(200);
            setRange(10, 100);
        }};

        private final ToggleSwitch enableRemapLButtonToggle = new ToggleSwitch();
        public final VKeyChooseButton remapLButtonActivateKeyBtn = new VKeyChooseButton();

        public BLBSettingStage(ExtendedI18n i18n) {
            super(i18n);
            getContent().getChildren().addAll(contentBuilder()
                    .divide(blbI18n.holdLButtonSetting.title)
                    .toggle(blbI18n.holdLButtonSetting.enable, enableHoldLButtonToggle)
                    .button(blbI18n.holdLButtonSetting.activateMethod, holdLButtonActivateMethodBtn)
                    .button(blbI18n.holdLButtonSetting.activateKey, holdLButtonActivateKeyBtn)
                    .divide(blbI18n.rapidlyClickLButtonSetting.title)
                    .toggle(blbI18n.rapidlyClickLButtonSetting.enable, enableRapidlyClickLButtonToggle)
                    .button(blbI18n.rapidlyClickLButtonSetting.activateMethod, rapidlyClickLButtonActivateMethodBtn)
                    .button(blbI18n.rapidlyClickLButtonSetting.activateKey, rapidlyClickLButtonActivateKeyBtn)
                    .slider(blbI18n.rapidlyClickLButtonSetting.triggerInterval, rapidlyClickLButtonTriggerInterval)
                    .divide(blbI18n.remapLButtonSetting.title)
                    .toggle(blbI18n.remapLButtonSetting.enable, enableRemapLButtonToggle)
                    .button(blbI18n.remapLButtonSetting.activateKey, remapLButtonActivateKeyBtn)
                    .build());
        }

        @Override
        public String getTitle() {
            return blbI18n.title;
        }

        @Override
        public void onVSettingStageInit() {
            enableHoldLButtonToggle.selectedProperty().set(blbConfig.holdLButtonSetting.enable);
            holdLButtonActivateMethodBtn.triggerModeProperty().set(blbConfig.holdLButtonSetting.activateMethod);
            holdLButtonActivateKeyBtn.keyProperty().set(blbConfig.holdLButtonSetting.activateKey);

            enableRapidlyClickLButtonToggle.selectedProperty().set(blbConfig.rapidlyClickLButtonSetting.enable);
            rapidlyClickLButtonActivateMethodBtn.triggerModeProperty().set(blbConfig.rapidlyClickLButtonSetting.activateMethod);
            rapidlyClickLButtonActivateKeyBtn.keyProperty().set(blbConfig.rapidlyClickLButtonSetting.activateKey);
            rapidlyClickLButtonTriggerInterval.setValue(blbConfig.rapidlyClickLButtonSetting.triggerInterval);

            enableRemapLButtonToggle.selectedProperty().set(blbConfig.remapLButtonSetting.enable);
            remapLButtonActivateKeyBtn.keyProperty().set(blbConfig.remapLButtonSetting.activateKey);
        }

        @Override
        public void onVSettingStageExit() {
            blbConfig.holdLButtonSetting.enable = enableHoldLButtonToggle.selectedProperty().get();
            blbConfig.holdLButtonSetting.activateMethod = holdLButtonActivateMethodBtn.triggerModeProperty().get();
            blbConfig.holdLButtonSetting.activateKey = holdLButtonActivateKeyBtn.keyProperty().get();

            blbConfig.rapidlyClickLButtonSetting.enable = enableRapidlyClickLButtonToggle.selectedProperty().get();
            blbConfig.rapidlyClickLButtonSetting.activateMethod = rapidlyClickLButtonActivateMethodBtn.triggerModeProperty().get();
            blbConfig.rapidlyClickLButtonSetting.activateKey = rapidlyClickLButtonActivateKeyBtn.keyProperty().get();
            blbConfig.rapidlyClickLButtonSetting.triggerInterval = rapidlyClickLButtonTriggerInterval.valueProperty().get();

            blbConfig.remapLButtonSetting.enable = enableRemapLButtonToggle.selectedProperty().get();
            blbConfig.remapLButtonSetting.activateKey = remapLButtonActivateKeyBtn.keyProperty().get();
        }
    }
}
