package club.pineclone.gtavops.gui.feature;

import club.pineclone.gtavops.config.ConfigHolder;
import club.pineclone.gtavops.config.Configuration;
import club.pineclone.gtavops.gui.component.VKeyChooseButton;
import club.pineclone.gtavops.gui.component.VOptionalButton;
import club.pineclone.gtavops.gui.component.VSettingStage;
import club.pineclone.gtavops.gui.forked.ForkedKeyChooser;
import club.pineclone.gtavops.gui.forked.ForkedSlider;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.i18n.I18nHolder;
import club.pineclone.gtavops.macro.SimpleMacro;
import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.action.impl.HoldLButtonAction;
import club.pineclone.gtavops.macro.action.impl.RapidlyClickLButtonAction;
import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerFactory;
import club.pineclone.gtavops.macro.trigger.TriggerIdentity;
import club.pineclone.gtavops.macro.trigger.TriggerMode;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.ui.toggle.ToggleSwitch;

public class _06BetterLButtonFeatureTogglePane extends FeatureTogglePane {

    ExtendedI18n i18n;
    ExtendedI18n.BetterLButton blbI18n;

    Configuration config;
    Configuration.BetterLButton blbConfig;

    private SimpleMacro holdLButtonMacro;
    private SimpleMacro rapidlyClickLButtonMacro;

    public _06BetterLButtonFeatureTogglePane() {
        i18n = I18nHolder.get();
        blbI18n = i18n.betterLButton;

        config = ConfigHolder.get();
        blbConfig = config.betterLButton;
    }

    @Override
    protected String getTitle() {
        return I18nHolder.get().betterLButton.title;
    }

    @Override
    protected void activate() {
        if (blbConfig.holdLButtonSetting.enable) {
            TriggerMode mode = getMode(blbConfig.holdLButtonSetting.activateMethod);
            Key activateKey = blbConfig.holdLButtonSetting.activateKey;
            Trigger trigger = TriggerFactory.simple(new TriggerIdentity(mode, activateKey));

            Action action = new HoldLButtonAction();
            holdLButtonMacro = new SimpleMacro(trigger, action);
            holdLButtonMacro.install();
        }

        if (blbConfig.rapidlyClickLButtonSetting.enable) {
            TriggerMode mode = getMode(blbConfig.rapidlyClickLButtonSetting.activateMethod);
            Key activateKey = blbConfig.rapidlyClickLButtonSetting.activateKey;
            long triggerInterval = (long) (Math.floor(blbConfig.rapidlyClickLButtonSetting.triggerInterval));

            Trigger trigger = TriggerFactory.simple(new TriggerIdentity(mode, activateKey));
            Action action = new RapidlyClickLButtonAction(triggerInterval);

            rapidlyClickLButtonMacro = new SimpleMacro(trigger, action);
            rapidlyClickLButtonMacro.install();
        }

    }

    private TriggerMode getMode(int triggerMethod) {
        return triggerMethod == 0 ? TriggerMode.HOLD : TriggerMode.TOGGLE;
    }

    @Override
    protected void deactivate() {
        if (holdLButtonMacro != null) holdLButtonMacro.uninstall();
        if (rapidlyClickLButtonMacro != null) rapidlyClickLButtonMacro.uninstall();
    }

    @Override
    public void init() {
        selectedProperty().set(blbConfig.baseSetting.enable);
    }

    @Override
    public void stop() {
        blbConfig.baseSetting.enable = selectedProperty().get();
        selectedProperty().set(false);
    }

    @Override
    public VSettingStage getSettingStage() {
        return new BLBSettingStage();
    }

    private class BLBSettingStage extends VSettingStage {

        private static final int FLAG_WITH_KEY_AND_MOUSE = ForkedKeyChooser.FLAG_WITH_KEY  | ForkedKeyChooser.FLAG_WITH_MOUSE;
        private static final int FLAG_WITH_ALL = FLAG_WITH_KEY_AND_MOUSE | ForkedKeyChooser.FLAG_WITH_WHEEL_SCROLL;

        private final ToggleSwitch enableHoldLButtonToggle = new ToggleSwitch();
        private final VKeyChooseButton holdLButtonActivateKeyBtn = new VKeyChooseButton(FLAG_WITH_ALL);
        private final VOptionalButton holdLButtonActivateMethodBtn = new VOptionalButton() {{
            addOptionalItem(i18n.hold);
            addOptionalItem(i18n.toggle);
        }};

        private final ToggleSwitch enableRapidlyClickLButtonToggle = new ToggleSwitch();
        private final VKeyChooseButton rapidlyClickLButtonActivateKeyBtn = new VKeyChooseButton(FLAG_WITH_ALL);
        private final VOptionalButton rapidlyClickLButtonActivateMethodBtn = new VOptionalButton() {{
            addOptionalItem(i18n.hold);
            addOptionalItem(i18n.toggle);
        }};
        private final ForkedSlider rapidlyClickLButtonTriggerInterval = new ForkedSlider() {{
            setLength(200);
            setRange(10, 100);
        }};


        public BLBSettingStage() {
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
                    .build());
        }

        @Override
        public String getTitle() {
            return blbI18n.title;
        }

        @Override
        public void init() {
            enableHoldLButtonToggle.selectedProperty().set(blbConfig.holdLButtonSetting.enable);
            holdLButtonActivateMethodBtn.indexProperty().set(blbConfig.holdLButtonSetting.activateMethod);
            holdLButtonActivateKeyBtn.keyProperty().set(blbConfig.holdLButtonSetting.activateKey);

            enableRapidlyClickLButtonToggle.selectedProperty().set(blbConfig.rapidlyClickLButtonSetting.enable);
            rapidlyClickLButtonActivateMethodBtn.indexProperty().set(blbConfig.rapidlyClickLButtonSetting.activateMethod);
            rapidlyClickLButtonActivateKeyBtn.keyProperty().set(blbConfig.rapidlyClickLButtonSetting.activateKey);
            rapidlyClickLButtonTriggerInterval.setValue(blbConfig.rapidlyClickLButtonSetting.triggerInterval);
        }

        @Override
        public void stop() {
            blbConfig.holdLButtonSetting.enable = enableHoldLButtonToggle.selectedProperty().get();
            blbConfig.holdLButtonSetting.activateMethod = holdLButtonActivateMethodBtn.indexProperty().get();
            blbConfig.holdLButtonSetting.activateKey = holdLButtonActivateKeyBtn.keyProperty().get();

            blbConfig.rapidlyClickLButtonSetting.enable = enableRapidlyClickLButtonToggle.selectedProperty().get();
            blbConfig.rapidlyClickLButtonSetting.activateMethod = rapidlyClickLButtonActivateMethodBtn.indexProperty().get();
            blbConfig.rapidlyClickLButtonSetting.activateKey = rapidlyClickLButtonActivateKeyBtn.keyProperty().get();
            blbConfig.rapidlyClickLButtonSetting.triggerInterval = rapidlyClickLButtonTriggerInterval.valueProperty().get();

        }
    }
}
