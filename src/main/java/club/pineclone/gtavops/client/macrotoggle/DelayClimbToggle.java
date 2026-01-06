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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/* 延迟攀爬 */
public class DelayClimbToggle extends MacroToggle {

    private UUID macroId;

    public DelayClimbToggle(ExtendedI18n i18n) {
        super(i18n);
    }

    @Override
    protected void onFeatureEnable() {
        macroId = MacroRegistry.getInstance().register(MacroConfigLoader.get(), MacroCreationStrategies.DELAY_CLIMB_MACRO_CREATION_STRATEGY);
        MacroRegistry.getInstance().launchMacro(macroId);
    }

    @Override
    protected void onFeatureDisable() {
        MacroRegistry.getInstance().terminateMacro(macroId);
    }

    @Override
    protected String getTitle() {
        return i18n.delayClimb.title;
    }

    @Override
    protected MacroSettingStage getSetting() {
        return new DCSettingStage(i18n);
    }

    @Override
    public void onUIInit() {
        selectedProperty().set(MacroConfigLoader.get().delayClimb.baseSetting.enable);
    }

    @Override
    public void onUIDispose() {
        log.debug("Save config to file: {}", selectedProperty().get());
        MacroConfigLoader.get().delayClimb.baseSetting.setEnable(selectedProperty().get());
    }

    private static class DCSettingStage
            extends MacroSettingStage
            implements ResourceHolder {

        private final ExtendedI18n.DelayClimb dcI18n = i18n.delayClimb;

        private final MacroConfig config = getConfig();
        private final MacroConfig.DelayClimb dcConfig = config.delayClimb;

        private static final int FLAG_WITH_KEY_AND_MOUSE = ForkedKeyChooser.FLAG_WITH_KEY  | ForkedKeyChooser.FLAG_WITH_MOUSE;

        private final VKeyChooseButton toggleDelayClimbKey = new VKeyChooseButton(FLAG_WITH_KEY_AND_MOUSE);
        private final VKeyChooseButton usePhoneKey = new VKeyChooseButton(FLAG_WITH_KEY_AND_MOUSE);
        private final VKeyChooseButton hideInCoverKey = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);

        private final ToggleSwitch hideInCoverOnExitToggle = new ToggleSwitch();  /* 是否在结束时尝试躲入掩体 */

        private final ForkedSlider timeUtilCameraExitedSlider = new ForkedSlider() {{
            setLength(300);
            setRange(200, 2000);
        }};

        private final ForkedSlider timeUtilCameraLoaded1Slider = new ForkedSlider() {{
            setLength(300);
            setRange(1000, 4000);
        }};

        private final ForkedSlider timeUtilCameraLoaded2Slider = new ForkedSlider() {{
            setLength(300);
            setRange(500, 3000);
        }};

        private final ForkedSlider triggerIntervalSlider = new ForkedSlider() {{
            setLength(300);
            setRange(1000, 4000);
        }};

        public DCSettingStage(ExtendedI18n i18n) {
            super(i18n);
            getContent().getChildren().addAll(contentBuilder()
                    .divide(dcI18n.baseSetting.title)
                    .button(dcI18n.baseSetting.usePhoneKey, usePhoneKey)
                    .button(dcI18n.baseSetting.hideInCoverKey, hideInCoverKey)
                    .button(dcI18n.baseSetting.toggleDelayClimbKey, toggleDelayClimbKey)
                    .slider(dcI18n.baseSetting.triggerInterval, triggerIntervalSlider)
                    .slider(dcI18n.baseSetting.timeUtilCameraExited, timeUtilCameraExitedSlider)
                    .slider(dcI18n.baseSetting.timeUtilCameraLoaded1, timeUtilCameraLoaded1Slider)
                    .slider(dcI18n.baseSetting.timeUtilCameraLoaded2, timeUtilCameraLoaded2Slider)
                    .toggle(dcI18n.baseSetting.hideInCoverOnExit, hideInCoverOnExitToggle)
                    .build());
        }

        @Override
        public String getTitle() {
            return dcI18n.title;
        }

        @Override
        public void onVSettingStageInit() {
            toggleDelayClimbKey.keyProperty().set(dcConfig.baseSetting.toggleDelayClimbKey);
            usePhoneKey.keyProperty().set(dcConfig.baseSetting.usePhoneKey);
            hideInCoverKey.keyProperty().set(dcConfig.baseSetting.hideInCoverKey);
            triggerIntervalSlider.setValue(dcConfig.baseSetting.triggerInterval);
            timeUtilCameraExitedSlider.setValue(dcConfig.baseSetting.timeUtilCameraExited);
            timeUtilCameraLoaded1Slider.setValue(dcConfig.baseSetting.timeUtilCameraLoaded1);
            timeUtilCameraLoaded2Slider.setValue(dcConfig.baseSetting.timeUtilCameraLoaded2);
            hideInCoverOnExitToggle.selectedProperty().set(dcConfig.baseSetting.hideInCoverOnExit);
        }

        @Override
        public void onVSettingStageExit() {
            dcConfig.baseSetting.toggleDelayClimbKey = toggleDelayClimbKey.keyProperty().get();
            dcConfig.baseSetting.usePhoneKey = usePhoneKey.keyProperty().get();
            dcConfig.baseSetting.hideInCoverKey = hideInCoverKey.keyProperty().get();
            dcConfig.baseSetting.triggerInterval = triggerIntervalSlider.valueProperty().get();
            dcConfig.baseSetting.timeUtilCameraExited = timeUtilCameraExitedSlider.valueProperty().get();
            dcConfig.baseSetting.timeUtilCameraLoaded1 = timeUtilCameraLoaded1Slider.valueProperty().get();
            dcConfig.baseSetting.timeUtilCameraLoaded2 = timeUtilCameraLoaded2Slider.valueProperty().get();
            dcConfig.baseSetting.hideInCoverOnExit = hideInCoverOnExitToggle.selectedProperty().get();
        }
    }
}
