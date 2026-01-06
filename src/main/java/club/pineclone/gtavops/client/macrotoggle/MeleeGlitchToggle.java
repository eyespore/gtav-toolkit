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

/* 近战偷速 */
public class MeleeGlitchToggle extends MacroToggle {

    private UUID macroId;

    public MeleeGlitchToggle(ExtendedI18n i18n) {
        super(i18n);
    }

    @Override
    protected void onFeatureEnable() {
        macroId = MacroRegistry.getInstance().register(MacroConfigLoader.get(), MacroCreationStrategies.MELEE_GLITCH_MACRO_CREATION_STRATEGY);
        MacroRegistry.getInstance().launchMacro(macroId);
    }

    @Override
    protected void onFeatureDisable() {
        MacroRegistry.getInstance().terminateMacro(macroId);
    }

    @Override
    protected String getTitle() {
        return i18n.meleeGlitch.title;
    }

    @Override
    protected MacroSettingStage getSetting() {
        return new MGSettingStage(i18n);
    }

    @Override
    public void onUIInit() {
        selectedProperty().set(MacroConfigLoader.get().meleeGlitch.baseSetting.enable);
    }

    @Override
    public void onUIDispose() {
        MacroConfigLoader.get().meleeGlitch.baseSetting.enable = selectedProperty().get();
    }

    private static class MGSettingStage
            extends MacroSettingStage
            implements ResourceHolder {

        private final ExtendedI18n.MeleeGlitch mgI18n = i18n.meleeGlitch;

        private final MacroConfig config = getConfig();
        private final MacroConfig.MeleeGlitch mgConfig = config.meleeGlitch;

        private static final int FLAG_WITH_KEY_AND_MOUSE = ForkedKeyChooser.FLAG_WITH_KEY  | ForkedKeyChooser.FLAG_WITH_MOUSE;
        private static final int FLAG_WITH_ALL = FLAG_WITH_KEY_AND_MOUSE | ForkedKeyChooser.FLAG_WITH_WHEEL_SCROLL;

        private final VTriggerModeChooseButton activateMethodBtn = new VTriggerModeChooseButton(
                VTriggerModeChooseButton.FLAG_WITH_HOLD | VTriggerModeChooseButton.FLAG_WITH_TOGGLE);
        private final VKeyChooseButton activateKeyBtn = new VKeyChooseButton(FLAG_WITH_ALL);
        private final VKeyChooseButton meleeSnakeScrollKeyBtn = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_WHEEL_SCROLL);
        private final ForkedSlider triggerIntervalSlider = new ForkedSlider() {{
            setLength(200);
            setRange(10, 50);
        }};

        private final ToggleSwitch enableSafetyKeyToggle = new ToggleSwitch();
        private final VKeyChooseButton safetyKeyBtn = new VKeyChooseButton(FLAG_WITH_ALL);

        public MGSettingStage(ExtendedI18n i18n) {
            super(i18n);
            getContent().getChildren().addAll(contentBuilder()
                    .divide(mgI18n.baseSetting.title)
                    .button(mgI18n.baseSetting.activateMethod, activateMethodBtn)
                    .button(mgI18n.baseSetting.activateKey, activateKeyBtn)
                    .slider(mgI18n.baseSetting.triggerInterval, triggerIntervalSlider)
                    .button(mgI18n.baseSetting.meleeSnakeScrollKey, meleeSnakeScrollKeyBtn)
                    .toggle(mgI18n.baseSetting.enableSafetyKey, enableSafetyKeyToggle)
                    .button(mgI18n.baseSetting.safetyKey, safetyKeyBtn)
                    .build());
        }


        @Override
        public void onVSettingStageInit() {
            activateMethodBtn.triggerModeProperty().set(mgConfig.baseSetting.activateMethod);
            activateKeyBtn.keyProperty().set(mgConfig.baseSetting.activatekey);
            meleeSnakeScrollKeyBtn.keyProperty().set(mgConfig.baseSetting.meleeSnakeScrollKey);
            triggerIntervalSlider.setValue(mgConfig.baseSetting.triggerInterval);

            enableSafetyKeyToggle.selectedProperty().set(mgConfig.baseSetting.enableSafetyKey);
            safetyKeyBtn.keyProperty().set(mgConfig.baseSetting.safetyKey);
        }

        @Override
        public void onVSettingStageExit() {
            mgConfig.baseSetting.activateMethod = activateMethodBtn.triggerModeProperty().get();
            mgConfig.baseSetting.activatekey = activateKeyBtn.keyProperty().get();
            mgConfig.baseSetting.triggerInterval = triggerIntervalSlider.valueProperty().get();
            mgConfig.baseSetting.meleeSnakeScrollKey = meleeSnakeScrollKeyBtn.keyProperty().get();

            mgConfig.baseSetting.enableSafetyKey = enableSafetyKeyToggle.selectedProperty().get();
            mgConfig.baseSetting.safetyKey = safetyKeyBtn.keyProperty().get();
        }

        @Override
        public String getTitle() {
            return mgI18n.title;
        }
    }
}
