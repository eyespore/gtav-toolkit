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

public class ADSwingToggle extends MacroToggle {

    private UUID macroId;

    public ADSwingToggle(ExtendedI18n i18n) {
        super(i18n);
    }

    @Override
    protected void onFeatureEnable() {
        macroId = MacroRegistry.getInstance().register(MacroConfigLoader.get(), MacroCreationStrategies.AD_SWING_MACRO_CREATION_STRATEGY);
        MacroRegistry.getInstance().launchMacro(macroId);
    }

    @Override
    protected void onFeatureDisable() {
        MacroRegistry.getInstance().terminateMacro(macroId);
    }

    @Override
    protected String getTitle() {
        return i18n.adSwing.title;
    }

    @Override
    protected MacroSettingStage getSetting() {
        return new ADWSettingStage(i18n);
    }

    @Override
    public void onUIInit() {
        selectedProperty().set(MacroConfigLoader.get().adSwing.baseSetting.enable);
    }

    @Override
    public void onUIDispose() {
        MacroConfigLoader.get().adSwing.baseSetting.enable = selectedProperty().get();
    }

    private static class ADWSettingStage
            extends MacroSettingStage
            implements ResourceHolder {
        private static final int FLAG_WITH_KEY_AND_MOUSE = ForkedKeyChooser.FLAG_WITH_KEY  | ForkedKeyChooser.FLAG_WITH_MOUSE;
        private static final int FLAG_WITH_ALL = FLAG_WITH_KEY_AND_MOUSE | ForkedKeyChooser.FLAG_WITH_WHEEL_SCROLL;

        MacroConfig config = getConfig();
        MacroConfig.ADSwing adwConfig = config.adSwing;
        ExtendedI18n.ADSwing adwI18n = i18n.adSwing;

        private final VTriggerModeChooseButton activateMethodBtn = new VTriggerModeChooseButton(
                VTriggerModeChooseButton.FLAG_WITH_HOLD | VTriggerModeChooseButton.FLAG_WITH_TOGGLE);

        private final VKeyChooseButton activateKeyBtn = new VKeyChooseButton(FLAG_WITH_ALL);
        private final VKeyChooseButton moveLeftKeyBtn = new VKeyChooseButton();
        private final VKeyChooseButton moveRightKeyBtn = new VKeyChooseButton();
        private final ForkedSlider triggerIntervalSlider = new ForkedSlider() {{
            setLength(200);
            setRange(10, 50);
        }};

        private final ToggleSwitch enableSafetyKeyToggle = new ToggleSwitch();
        private final VKeyChooseButton safetyKeyBtn = new VKeyChooseButton(FLAG_WITH_ALL);

        public ADWSettingStage(ExtendedI18n i18n) {
            super(i18n);
            getContent().getChildren().addAll(contentBuilder()
                     .divide(adwI18n.baseSetting.title)
                    .button(adwI18n.baseSetting.activateMethod, activateMethodBtn)
                    .button(adwI18n.baseSetting.activateKey, activateKeyBtn)
                    .slider(adwI18n.baseSetting.triggerInterval, triggerIntervalSlider)
                    .button(adwI18n.baseSetting.moveLeftKey, moveLeftKeyBtn)
                    .button(adwI18n.baseSetting.moveRightKey, moveRightKeyBtn)
                    .toggle(adwI18n.baseSetting.enableSafetyKey, enableSafetyKeyToggle)
                    .button(adwI18n.baseSetting.safetyKey, safetyKeyBtn)
                    .build());
        }

        @Override
        public void onVSettingStageInit() {
            activateMethodBtn.triggerModeProperty().set(adwConfig.baseSetting.activateMethod);
            activateKeyBtn.keyProperty().set(adwConfig.baseSetting.activatekey);
            moveLeftKeyBtn.keyProperty().set(adwConfig.baseSetting.moveLeftKey);
            moveRightKeyBtn.keyProperty().set(adwConfig.baseSetting.moveRightKey);
            triggerIntervalSlider.setValue(adwConfig.baseSetting.triggerInterval);

            enableSafetyKeyToggle.selectedProperty().set(adwConfig.baseSetting.enableSafetyKey);
            safetyKeyBtn.keyProperty().set(adwConfig.baseSetting.safetyKey);
        }

        @Override
        public void onVSettingStageExit() {
            adwConfig.baseSetting.activateMethod = activateMethodBtn.triggerModeProperty().get();
            adwConfig.baseSetting.activatekey = activateKeyBtn.keyProperty().get();
            adwConfig.baseSetting.moveLeftKey = moveLeftKeyBtn.keyProperty().get();
            adwConfig.baseSetting.moveRightKey = moveRightKeyBtn.keyProperty().get();
            adwConfig.baseSetting.triggerInterval = triggerIntervalSlider.valueProperty().get();

            adwConfig.baseSetting.enableSafetyKey = enableSafetyKeyToggle.selectedProperty().get();
            adwConfig.baseSetting.safetyKey = safetyKeyBtn.keyProperty().get();
        }

        @Override
        public String getTitle() {
            return i18n.adSwing.title;
        }
    }
}
