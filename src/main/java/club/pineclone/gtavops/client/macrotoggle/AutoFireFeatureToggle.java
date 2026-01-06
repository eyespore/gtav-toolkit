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

import java.util.UUID;

/* 连发RPG */
public class AutoFireFeatureToggle extends MacroToggle {

    private UUID macroId;  /* 宏ID */

    public AutoFireFeatureToggle(ExtendedI18n i18n) {
        super(i18n);
    }

    @Override
    protected void onFeatureEnable() {
        macroId = MacroRegistry.getInstance().register(MacroConfigLoader.get(), MacroCreationStrategies.AUTO_FIRE_MACRO_CREATION_STRATEGY);
        MacroRegistry.getInstance().launchMacro(macroId);
    }

    @Override
    protected void onFeatureDisable() {
        MacroRegistry.getInstance().terminateMacro(macroId);
    }

    @Override
    protected String getTitle() {
        return i18n.autoFire.title;
    }

    @Override
    protected MacroSettingStage getSetting() {
        return new AutoFireSettingStage(i18n);
    }

    @Override
    public void onUIInit() {
        selectedProperty().set(MacroConfigLoader.get().autoFire.baseSetting.enable);
    }

    @Override
    public void onUIDispose() {
        MacroConfigLoader.get().autoFire.baseSetting.enable = selectedProperty().get();
    }

    private static class AutoFireSettingStage
            extends MacroSettingStage
            implements ResourceHolder {

        private final ExtendedI18n.AutoFire aRpgI18n = i18n.autoFire;
        private final MacroConfig config = getConfig();
        private final MacroConfig.AutoFire aRpgConfig = config.autoFire;

        private static final int FLAG_WITH_KEY_AND_MOUSE = ForkedKeyChooser.FLAG_WITH_KEY  | ForkedKeyChooser.FLAG_WITH_MOUSE;

        /* Join A New Session */
        private final VKeyChooseButton activateKeyBtn = new VKeyChooseButton(FLAG_WITH_KEY_AND_MOUSE);
        private final VKeyChooseButton heavyWeaponKeyBtn = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);
        private final VKeyChooseButton specialWeaponKeyBtn = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);

        private final VTriggerModeChooseButton activateMethodBtn = new VTriggerModeChooseButton(VTriggerModeChooseButton.FLAG_WITH_TOGGLE | VTriggerModeChooseButton.FLAG_WITH_HOLD);

        private final ForkedSlider triggerIntervalSlider = new ForkedSlider() {{
            setLength(300);
            setRange(200, 1200);
        }};

        private final ForkedSlider mousePressIntervalSlider = new ForkedSlider() {{
            setLength(300);
            setRange(200, 1000);
        }};

        public AutoFireSettingStage(ExtendedI18n i18n) {
            super(i18n);
            getContent().getChildren().addAll(contentBuilder()
                    .divide(aRpgI18n.baseSetting.title)
                    .button(aRpgI18n.baseSetting.activateMethod, activateMethodBtn)
                    .button(aRpgI18n.baseSetting.activateKey, activateKeyBtn)
                    .button(aRpgI18n.baseSetting.heavyWeaponKey, heavyWeaponKeyBtn)
                    .button(aRpgI18n.baseSetting.specialWeaponKey, specialWeaponKeyBtn)
                    .slider(aRpgI18n.baseSetting.triggerInterval, triggerIntervalSlider)
                    .slider(aRpgI18n.baseSetting.mousePressInterval, mousePressIntervalSlider)
                    .build());
        }

        @Override
        public String getTitle() {
            return aRpgI18n.title;
        }

        @Override
        public void onVSettingStageInit() {
            activateMethodBtn.triggerModeProperty().set(aRpgConfig.baseSetting.activateMethod);
            heavyWeaponKeyBtn.keyProperty().set(aRpgConfig.baseSetting.heavyWeaponKey);
            specialWeaponKeyBtn.keyProperty().set(aRpgConfig.baseSetting.specialWeaponKey);
            activateKeyBtn.keyProperty().set(aRpgConfig.baseSetting.activateKey);
            triggerIntervalSlider.setValue(aRpgConfig.baseSetting.triggerInterval);
            mousePressIntervalSlider.setValue(aRpgConfig.baseSetting.mousePressInterval);
        }

        @Override
        public void onVSettingStageExit() {
            aRpgConfig.baseSetting.activateMethod = activateMethodBtn.triggerModeProperty().get();
            aRpgConfig.baseSetting.activateKey = activateKeyBtn.keyProperty().get();
            aRpgConfig.baseSetting.heavyWeaponKey = heavyWeaponKeyBtn.keyProperty().get();
            aRpgConfig.baseSetting.specialWeaponKey = specialWeaponKeyBtn.keyProperty().get();
            aRpgConfig.baseSetting.triggerInterval = triggerIntervalSlider.valueProperty().get();
            aRpgConfig.baseSetting.mousePressInterval = mousePressIntervalSlider.valueProperty().get();
        }
    }
}
