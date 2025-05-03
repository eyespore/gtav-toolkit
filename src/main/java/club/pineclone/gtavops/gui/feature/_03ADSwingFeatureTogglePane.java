package club.pineclone.gtavops.gui.feature;

import club.pineclone.gtavops.config.ConfigHolder;
import club.pineclone.gtavops.config.Configuration;
import club.pineclone.gtavops.gui.component.KeyChooseButton;
import club.pineclone.gtavops.gui.component.OptionalButton;
import club.pineclone.gtavops.gui.component.SettingStage;
import club.pineclone.gtavops.gui.forked.ForkedKeyChooser;
import club.pineclone.gtavops.gui.forked.ForkedSlider;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.i18n.I18nHolder;
import club.pineclone.gtavops.macro.SimpleMacro;
import club.pineclone.gtavops.macro.trigger.TriggerFactory;
import club.pineclone.gtavops.macro.trigger.TriggerIdentity;
import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.action.adswing.ADSwingAction;
import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerMode;
import io.vproxy.vfx.entity.input.Key;

public class _03ADSwingFeatureTogglePane extends FeatureTogglePane {

    Configuration config;
    Configuration.ADSwing adwConfig;
    ExtendedI18n i18n;
    ExtendedI18n.ADSwing adwI18n;

    private SimpleMacro bindings;

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
        bindings = new SimpleMacro(trigger, action);
        bindings.install();
    }

    private Trigger buildTrigger() {
        TriggerMode mode = adwConfig.activateMethod == 0 ? TriggerMode.HOLD : TriggerMode.TOGGLE;  /* 激活模式 切换执行 or 按住执行 */
        Key activatekey = adwConfig.activatekey;  /* 激活热键 */
        return TriggerFactory.getTrigger(new TriggerIdentity(activatekey, mode));  /* 触发器 */
    }

    private Action buildAction() {
        long triggerInterval = (long) Math.floor(adwConfig.triggerInterval);
        Key moveLeftKey = adwConfig.moveLeftKey;
        Key moveRightKey = adwConfig.moveRightKey;
        return new ADSwingAction(triggerInterval, moveLeftKey, moveRightKey);
    }

    @Override
    protected void deactivate() {
        bindings.uninstall();
    }

    @Override
    public void init() {
        selectedProperty().set(adwConfig.enable);
    }

    @Override
    public void stop() {
        adwConfig.enable = selectedProperty().get();
        selectedProperty().set(false);
    }

    @Override
    public SettingStage getSettingStage() {
        return new ADWSettingStage();
    }

    private class ADWSettingStage extends SettingStage {
        private static final int FLAG_WITH_KEY_AND_MOUSE = ForkedKeyChooser.FLAG_WITH_KEY  | ForkedKeyChooser.FLAG_WITH_MOUSE;
        private static final int FLAG_WITH_ALL = FLAG_WITH_KEY_AND_MOUSE | ForkedKeyChooser.FLAG_WITH_WHEEL_SCROLL;

        private final OptionalButton activateMethodBtn = new OptionalButton() {{
            addOptionalItem(i18n.hold);
            addOptionalItem(i18n.toggle);
        }};
        private final KeyChooseButton activateKeyBtn = new KeyChooseButton(FLAG_WITH_ALL);
        private final KeyChooseButton moveLeftKeyBtn = new KeyChooseButton();
        private final KeyChooseButton moveRightKeyBtn = new KeyChooseButton();
        private final ForkedSlider triggerIntervalSlider = new ForkedSlider() {{
            setLength(200);
            setRange(10, 50);
        }};

        public ADWSettingStage() {
            super();
            getContent().getChildren().addAll(contentBuilder()
                    .button(adwI18n.activateMethod, activateMethodBtn)
                    .button(adwI18n.activateKey, activateKeyBtn)
                    .slider(adwI18n.triggerInterval, triggerIntervalSlider)
                    .button(adwI18n.moveLeftKey, moveLeftKeyBtn)
                    .button(adwI18n.moveRightKey, moveRightKeyBtn)
                    .build());
        }

        @Override
        public void init() {
            activateMethodBtn.indexProperty().set(adwConfig.activateMethod);
            activateKeyBtn.keyProperty().set(adwConfig.activatekey);
            moveLeftKeyBtn.keyProperty().set(adwConfig.moveLeftKey);
            moveRightKeyBtn.keyProperty().set(adwConfig.moveRightKey);
            triggerIntervalSlider.setValue(adwConfig.triggerInterval);
        }

        @Override
        public void stop() {
            adwConfig.activateMethod = activateMethodBtn.indexProperty().get();
            adwConfig.activatekey = activateKeyBtn.keyProperty().get();
            adwConfig.moveLeftKey = moveLeftKeyBtn.keyProperty().get();
            adwConfig.moveRightKey = moveRightKeyBtn.keyProperty().get();
            adwConfig.triggerInterval = triggerIntervalSlider.valueProperty().get();
        }

        @Override
        public String getTitle() {
            return adwI18n.title;
        }
    }
}
