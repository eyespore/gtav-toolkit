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
import club.pineclone.gtavops.macro.trigger.TriggerFactory;
import club.pineclone.gtavops.macro.trigger.TriggerIdentity;
import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.action.impl.ADSwingAction;
import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerMode;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.ui.toggle.ToggleSwitch;

public class _03ADSwingFeatureTogglePane extends FeatureTogglePane {

    Configuration config;
    Configuration.ADSwing adwConfig;
    ExtendedI18n i18n;
    ExtendedI18n.ADSwing adwI18n;

    private SimpleMacro macro;

    public _03ADSwingFeatureTogglePane() {
        this.config = ConfigHolder.get();
        this.adwConfig = config.adSwing;

        this.i18n = I18nHolder.get();
        this.adwI18n = i18n.adSwing;
    }

    @Override
    protected String getTitle() {
        return I18nHolder.get().adSwing.title;
    }

    @Override
    protected void activate() {
        Trigger trigger = buildTrigger();
        Action action = buildAction();
        macro = new SimpleMacro(trigger, action);
        macro.install();
    }

    private Trigger buildTrigger() {
        TriggerMode mode = adwConfig.baseSetting.activateMethod == 0 ? TriggerMode.HOLD : TriggerMode.TOGGLE;  /* 激活模式 切换执行 or 按住执行 */

        Key activatekey = adwConfig.baseSetting.activatekey;  /* 激活热键 */
        if (adwConfig.baseSetting.enableSafetyKey) {
            Key safetyKey = adwConfig.baseSetting.safetyKey;
            return TriggerFactory.composite(
                    new TriggerIdentity(mode, activatekey),
                    new TriggerIdentity(mode, safetyKey)
            );
        }

        return TriggerFactory.simple(new TriggerIdentity(mode, activatekey));  /* 触发器 */
    }

    private Action buildAction() {
        long triggerInterval = (long) Math.floor(adwConfig.baseSetting.triggerInterval);
        Key moveLeftKey = adwConfig.baseSetting.moveLeftKey;
        Key moveRightKey = adwConfig.baseSetting.moveRightKey;
        return new ADSwingAction(triggerInterval, moveLeftKey, moveRightKey);
    }

    @Override
    protected void deactivate() {
        macro.uninstall();
    }

    @Override
    public void init() {
        selectedProperty().set(adwConfig.baseSetting.enable);
    }

    @Override
    public void stop() {
        adwConfig.baseSetting.enable = selectedProperty().get();
        selectedProperty().set(false);
    }

    @Override
    public VSettingStage getSettingStage() {
        return new ADWSettingStage();
    }

    private class ADWSettingStage extends VSettingStage {
        private static final int FLAG_WITH_KEY_AND_MOUSE = ForkedKeyChooser.FLAG_WITH_KEY  | ForkedKeyChooser.FLAG_WITH_MOUSE;
        private static final int FLAG_WITH_ALL = FLAG_WITH_KEY_AND_MOUSE | ForkedKeyChooser.FLAG_WITH_WHEEL_SCROLL;

        private final VOptionalButton activateMethodBtn = new VOptionalButton() {{
            addOptionalItem(i18n.hold);
            addOptionalItem(i18n.toggle);
        }};
        private final VKeyChooseButton activateKeyBtn = new VKeyChooseButton(FLAG_WITH_ALL);
        private final VKeyChooseButton moveLeftKeyBtn = new VKeyChooseButton();
        private final VKeyChooseButton moveRightKeyBtn = new VKeyChooseButton();
        private final ForkedSlider triggerIntervalSlider = new ForkedSlider() {{
            setLength(200);
            setRange(10, 50);
        }};

        private final ToggleSwitch enableSafetyKeyToggle = new ToggleSwitch();
        private final VKeyChooseButton safetyKeyBtn = new VKeyChooseButton(FLAG_WITH_ALL);

        public ADWSettingStage() {
            super();
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
        public void init() {
            activateMethodBtn.indexProperty().set(adwConfig.baseSetting.activateMethod);
            activateKeyBtn.keyProperty().set(adwConfig.baseSetting.activatekey);
            moveLeftKeyBtn.keyProperty().set(adwConfig.baseSetting.moveLeftKey);
            moveRightKeyBtn.keyProperty().set(adwConfig.baseSetting.moveRightKey);
            triggerIntervalSlider.setValue(adwConfig.baseSetting.triggerInterval);

            enableSafetyKeyToggle.selectedProperty().set(adwConfig.baseSetting.enableSafetyKey);
            safetyKeyBtn.keyProperty().set(adwConfig.baseSetting.safetyKey);
        }

        @Override
        public void stop() {
            adwConfig.baseSetting.activateMethod = activateMethodBtn.indexProperty().get();
            adwConfig.baseSetting.activatekey = activateKeyBtn.keyProperty().get();
            adwConfig.baseSetting.moveLeftKey = moveLeftKeyBtn.keyProperty().get();
            adwConfig.baseSetting.moveRightKey = moveRightKeyBtn.keyProperty().get();
            adwConfig.baseSetting.triggerInterval = triggerIntervalSlider.valueProperty().get();

            adwConfig.baseSetting.enableSafetyKey = enableSafetyKeyToggle.selectedProperty().get();
            adwConfig.baseSetting.safetyKey = safetyKeyBtn.keyProperty().get();
        }

        @Override
        public String getTitle() {
            return adwI18n.title;
        }
    }
}
