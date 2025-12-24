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
import club.pineclone.gtavops.macro.action.impl.DelayClimbAction;
import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerFactory;
import club.pineclone.gtavops.macro.trigger.TriggerIdentity;
import club.pineclone.gtavops.macro.trigger.TriggerMode;
import io.vproxy.vfx.entity.input.Key;

import java.util.UUID;

/* 延迟攀爬 */
public class _08DelayClimbFeatureTogglePane
        extends FeatureTogglePane
        implements ResourceHolder {

    public _08DelayClimbFeatureTogglePane() {
        super(new DelayClimbFeatureContext(), new DCSettingStage());
    }

    @Override
    protected String getTitle() {
        return getI18n().delayClimb.title;
    }

    @Override
    public boolean init() {
        return getConfig().delayClimb.baseSetting.enable;
    }

    @Override
    public void stop(boolean enabled) {
        getConfig().delayClimb.baseSetting.enable = enabled;
    }

    private static class DelayClimbFeatureContext
            extends FeatureContext
            implements ResourceHolder, MacroContextHolder {

        private UUID macroId;
        private final Config config = getConfig();
        private final Config.DelayClimb dcconfig = config.delayClimb;

        @Override
        protected void activate() {
            Key toggleDelayClimbKey = dcconfig.baseSetting.toggleDelayClimbKey;
            Key usePhoneKey = dcconfig.baseSetting.usePhoneKey;
            Key hideInCoverKey = dcconfig.baseSetting.hideInCoverKey;

            /* 自由活动的时间间隔 */
            long triggerInterval = (long) (Math.floor(dcconfig.baseSetting.triggerInterval));

            /* 等待相机退出的时间间隔 */
            long timeUtilCameraExited = (long) (Math.floor(dcconfig.baseSetting.timeUtilCameraExited));
            long timeUtilCameraLoaded1 = (long) (Math.floor(dcconfig.baseSetting.timeUtilCameraLoaded1));
            long timeUtilCameraLoaded2 = (long) (Math.floor(dcconfig.baseSetting.timeUtilCameraLoaded2));


            Trigger trigger = TriggerFactory.simple(new TriggerIdentity(TriggerMode.CLICK, toggleDelayClimbKey));
            Action action = new DelayClimbAction(
                    usePhoneKey, hideInCoverKey, triggerInterval,
                    timeUtilCameraExited, timeUtilCameraLoaded1, timeUtilCameraLoaded2);

            macroId = MACRO_FACTORY.createSimpleMacro(trigger, action);
            MACRO_REGISTRY.install(macroId);
        }

        @Override
        protected void deactivate() {
            MACRO_REGISTRY.uninstall(macroId);
        }
    }

    private static class DCSettingStage
            extends VSettingStage
            implements ResourceHolder {

        private final ExtendedI18n i18n = getI18n();
        private final ExtendedI18n.DelayClimb dcI18n = i18n.delayClimb;

        private final Config config = getConfig();
        private final Config.DelayClimb dcConfig = config.delayClimb;

        private static final int FLAG_WITH_KEY_AND_MOUSE = ForkedKeyChooser.FLAG_WITH_KEY  | ForkedKeyChooser.FLAG_WITH_MOUSE;
        private static final int FLAG_WITH_ALL = FLAG_WITH_KEY_AND_MOUSE | ForkedKeyChooser.FLAG_WITH_WHEEL_SCROLL;

        private final VKeyChooseButton toggleDelayClimbKey = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);
        private final VKeyChooseButton usePhoneKey = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);
        private final VKeyChooseButton hideInCoverKey = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);

        private final ForkedSlider timeUtilCameraExitedSlider = new ForkedSlider() {{
            setLength(400);
            setRange(200, 2000);
        }};

        private final ForkedSlider timeUtilCameraLoaded1Slider = new ForkedSlider() {{
            setLength(400);
            setRange(1000, 4000);
        }};

        private final ForkedSlider timeUtilCameraLoaded2Slider = new ForkedSlider() {{
            setLength(400);
            setRange(500, 3000);
        }};

        private final ForkedSlider triggerIntervalSlider = new ForkedSlider() {{
            setLength(400);
            setRange(1000, 4000);
        }};

        public DCSettingStage() {
            getContent().getChildren().addAll(contentBuilder()
                    .divide(dcI18n.baseSetting.title)
                    .button(dcI18n.baseSetting.usePhoneKey, usePhoneKey)
                    .button(dcI18n.baseSetting.hideInCoverKey, hideInCoverKey)
                    .button(dcI18n.baseSetting.toggleDelayClimbKey, toggleDelayClimbKey)
                    .slider(dcI18n.baseSetting.triggerInterval, triggerIntervalSlider)
                    .slider(dcI18n.baseSetting.timeUtilCameraExited, timeUtilCameraExitedSlider)
                    .slider(dcI18n.baseSetting.timeUtilCameraLoaded1, timeUtilCameraLoaded1Slider)
                    .slider(dcI18n.baseSetting.timeUtilCameraLoaded2, timeUtilCameraLoaded2Slider)
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
        }
    }
}
