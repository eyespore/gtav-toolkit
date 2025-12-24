package club.pineclone.gtavops.gui.feature;

import club.pineclone.gtavops.common.ResourceHolder;
import club.pineclone.gtavops.config.Config;
import club.pineclone.gtavops.gui.component.TriggerModeButton;
import club.pineclone.gtavops.gui.component.VKeyChooseButton;
import club.pineclone.gtavops.gui.component.VSettingStage;
import club.pineclone.gtavops.gui.forked.ForkedKeyChooser;
import club.pineclone.gtavops.gui.forked.ForkedSlider;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.macro.MacroContextHolder;
import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.action.impl.MeleeGlitchAction;
import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerFactory;
import club.pineclone.gtavops.macro.trigger.TriggerIdentity;
import club.pineclone.gtavops.macro.trigger.TriggerMode;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.ui.toggle.ToggleSwitch;

import java.util.UUID;

/* 近战偷速 */
public class _04MeleeGlitchFeatureTogglePane
        extends FeatureTogglePane
        implements ResourceHolder {

    public _04MeleeGlitchFeatureTogglePane() {
        super(new MGFeatureContext(), new MGSettingStage());
    }

    @Override
    protected String getTitle() {
        return getI18n().meleeGlitch.title;
    }

    @Override
    public boolean init() {
        return getConfig().meleeGlitch.baseSetting.enable;
    }

    @Override
    public void stop(boolean enabled) {
        getConfig().meleeGlitch.baseSetting.enable = enabled;
    }

    private static class MGFeatureContext
            extends FeatureContext
            implements ResourceHolder, MacroContextHolder {

        private UUID macroId;

        private final Config config = getConfig();
        private final Config.MeleeGlitch mgConfig = config.meleeGlitch;

        @Override
        protected void activate() {
            Trigger trigger = buildTrigger();
            Action action = buildAction();
            macroId = MACRO_FACTORY.createSimpleMacro(trigger, action);
            MACRO_REGISTRY.install(macroId);
        }

        private Trigger buildTrigger() {
            TriggerMode mode = mgConfig.baseSetting.activateMethod;  /* 激活模式 切换执行 or 按住执行 */
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
            MACRO_REGISTRY.uninstall(macroId);
        }
    }

    private static class MGSettingStage
            extends VSettingStage
            implements ResourceHolder {

        private final ExtendedI18n i18n = getI18n();
        private final ExtendedI18n.MeleeGlitch mgI18n = i18n.meleeGlitch;

        private final Config config = getConfig();
        private final Config.MeleeGlitch mgConfig = config.meleeGlitch;

        private static final int FLAG_WITH_KEY_AND_MOUSE = ForkedKeyChooser.FLAG_WITH_KEY  | ForkedKeyChooser.FLAG_WITH_MOUSE;
        private static final int FLAG_WITH_ALL = FLAG_WITH_KEY_AND_MOUSE | ForkedKeyChooser.FLAG_WITH_WHEEL_SCROLL;

        private final TriggerModeButton activateMethodBtn = new TriggerModeButton(
                TriggerModeButton.FLAG_WITH_HOLD | TriggerModeButton.FLAG_WITH_TOGGLE);
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
