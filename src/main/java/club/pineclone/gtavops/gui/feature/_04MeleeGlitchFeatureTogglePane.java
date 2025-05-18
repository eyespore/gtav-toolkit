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
import club.pineclone.gtavops.macro.action.impl.MeleeGlitchAction;
import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerFactory;
import club.pineclone.gtavops.macro.trigger.TriggerIdentity;
import club.pineclone.gtavops.macro.trigger.TriggerMode;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.ui.toggle.ToggleSwitch;

/* 近战偷速 */
public class _04MeleeGlitchFeatureTogglePane extends FeatureTogglePane {

    ExtendedI18n i18n;
    ExtendedI18n.MeleeGlitch mgI18n;

    Configuration config;
    Configuration.MeleeGlitch mgConfig;

    private SimpleMacro macro;

    public _04MeleeGlitchFeatureTogglePane() {
        i18n = I18nHolder.get();
        mgI18n = i18n.meleeGlitch;

        config = ConfigHolder.get();
        mgConfig = config.meleeGlitch;
    }

    @Override
    protected String getTitle() {
        return I18nHolder.get().meleeGlitch.title;
    }

    @Override
    protected void activate() {
        Trigger trigger = buildTrigger();
        Action action = buildAction();
        macro = new SimpleMacro(trigger, action);
        macro.install();
    }

    private Trigger buildTrigger() {
        TriggerMode mode = mgConfig.baseSetting.activateMethod == 0 ? TriggerMode.HOLD : TriggerMode.TOGGLE;  /* 激活模式 切换执行 or 按住执行 */
        Key activatekey = mgConfig.baseSetting.activatekey;  /* 激活热键 */

        if (mgConfig.baseSetting.enableSafetyKey) {
            Key safetyKey = mgConfig.baseSetting.safetyKey;
            return TriggerFactory.composite(
                    new TriggerIdentity(mode, activatekey),
                    new TriggerIdentity(mode, safetyKey)
            );
        }

        return TriggerFactory.simple(new TriggerIdentity(mode, activatekey));  /* 触发器 */
    }

    private Action buildAction() {
        long triggerInterval = (long) Math.floor(mgConfig.baseSetting.triggerInterval);
        Key meleeSnakeScrollKey = mgConfig.baseSetting.meleeSnakeScrollKey;
        return new MeleeGlitchAction(triggerInterval, meleeSnakeScrollKey);
    }

    @Override
    protected void deactivate() {
        macro.uninstall();
    }

    @Override
    public void init() {
        selectedProperty().set(mgConfig.baseSetting.enable);
    }

    @Override
    public void stop() {
        mgConfig.baseSetting.enable = selectedProperty().get();
        selectedProperty().set(false);
    }

    @Override
    public VSettingStage getSettingStage() {
        return new MGSettingStage();
    }

    private class MGSettingStage extends VSettingStage {

        private static final int FLAG_WITH_KEY_AND_MOUSE = ForkedKeyChooser.FLAG_WITH_KEY  | ForkedKeyChooser.FLAG_WITH_MOUSE;
        private static final int FLAG_WITH_ALL = FLAG_WITH_KEY_AND_MOUSE | ForkedKeyChooser.FLAG_WITH_WHEEL_SCROLL;

        private final VOptionalButton activateMethodBtn = new VOptionalButton() {{
            addOptionalItem(i18n.hold);
            addOptionalItem(i18n.toggle);
        }};
        private final VKeyChooseButton activateKeyBtn = new VKeyChooseButton(FLAG_WITH_ALL);
        private final VKeyChooseButton meleeSnakeScrollKeyBtn = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_WHEEL_SCROLL);
        private final ForkedSlider triggerIntervalSlider = new ForkedSlider() {{
            setLength(200);
            setRange(10, 50);
        }};

        private final ToggleSwitch enableSafetyKeyToggle = new ToggleSwitch();
        private final VKeyChooseButton safetyKeyBtn = new VKeyChooseButton(FLAG_WITH_ALL);

        public MGSettingStage() {
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
        public void init() {
            activateMethodBtn.indexProperty().set(mgConfig.baseSetting.activateMethod);
            activateKeyBtn.keyProperty().set(mgConfig.baseSetting.activatekey);
            meleeSnakeScrollKeyBtn.keyProperty().set(mgConfig.baseSetting.meleeSnakeScrollKey);
            triggerIntervalSlider.setValue(mgConfig.baseSetting.triggerInterval);

            enableSafetyKeyToggle.selectedProperty().set(mgConfig.baseSetting.enableSafetyKey);
            safetyKeyBtn.keyProperty().set(mgConfig.baseSetting.safetyKey);
        }

        @Override
        public void stop() {
            mgConfig.baseSetting.activateMethod = activateMethodBtn.indexProperty().get();
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
