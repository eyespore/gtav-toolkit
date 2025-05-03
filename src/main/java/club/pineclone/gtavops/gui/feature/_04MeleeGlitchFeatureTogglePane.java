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
import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.action.meleeGlitch.MeleeGlitchAction;
import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerFactory;
import club.pineclone.gtavops.macro.trigger.TriggerIdentity;
import club.pineclone.gtavops.macro.trigger.TriggerMode;
import io.vproxy.vfx.entity.input.Key;

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
        TriggerMode mode = mgConfig.activateMethod == 0 ? TriggerMode.HOLD : TriggerMode.TOGGLE;  /* 激活模式 切换执行 or 按住执行 */
        Key activatekey = mgConfig.activatekey;  /* 激活热键 */
        return TriggerFactory.getTrigger(new TriggerIdentity(activatekey, mode));  /* 触发器 */
    }

    private Action buildAction() {
        long triggerInterval = (long) Math.floor(mgConfig.triggerInterval);
        Key meleeSnakeScrollKey = mgConfig.meleeSnakeScrollKey;
        return new MeleeGlitchAction(triggerInterval, meleeSnakeScrollKey);
    }

    @Override
    protected void deactivate() {
        macro.uninstall();
    }

    @Override
    public void init() {
        selectedProperty().set(mgConfig.enable);
    }

    @Override
    public void stop() {
        mgConfig.enable = selectedProperty().get();
        selectedProperty().set(false);
    }

    @Override
    public SettingStage getSettingStage() {
        return new MGSettingStage();
    }

    private class MGSettingStage extends SettingStage {

        private static final int FLAG_WITH_KEY_AND_MOUSE = ForkedKeyChooser.FLAG_WITH_KEY  | ForkedKeyChooser.FLAG_WITH_MOUSE;
        private static final int FLAG_WITH_ALL = FLAG_WITH_KEY_AND_MOUSE | ForkedKeyChooser.FLAG_WITH_WHEEL_SCROLL;

        private final OptionalButton activateMethodBtn = new OptionalButton() {{
            addOptionalItem(i18n.hold);
            addOptionalItem(i18n.toggle);
        }};
        private final KeyChooseButton activateKeyBtn = new KeyChooseButton(FLAG_WITH_ALL);
        private final KeyChooseButton meleeSnakeScrollKeyBtn = new KeyChooseButton(ForkedKeyChooser.FLAG_WITH_WHEEL_SCROLL);
        private final ForkedSlider triggerIntervalSlider = new ForkedSlider() {{
            setLength(200);
            setRange(10, 50);
        }};

        public MGSettingStage() {
            getContent().getChildren().addAll(contentBuilder()
                    .button(mgI18n.activateMethod, activateMethodBtn)
                    .button(mgI18n.activateKey, activateKeyBtn)
                    .slider(mgI18n.triggerInterval, triggerIntervalSlider)
                    .button(mgI18n.meleeSnakeScrollKey, meleeSnakeScrollKeyBtn)
                    .build());
        }


        @Override
        public void init() {
            activateMethodBtn.indexProperty().set(mgConfig.activateMethod);
            activateKeyBtn.keyProperty().set(mgConfig.activatekey);
            meleeSnakeScrollKeyBtn.keyProperty().set(mgConfig.meleeSnakeScrollKey);
            triggerIntervalSlider.setValue(mgConfig.triggerInterval);
        }

        @Override
        public void stop() {
            mgConfig.activateMethod = activateMethodBtn.indexProperty().get();
            mgConfig.activatekey = activateKeyBtn.keyProperty().get();
            mgConfig.triggerInterval = triggerIntervalSlider.valueProperty().get();
            mgConfig.meleeSnakeScrollKey = meleeSnakeScrollKeyBtn.keyProperty().get();
        }

        @Override
        public String getTitle() {
            return mgI18n.title;
        }

    }
}
