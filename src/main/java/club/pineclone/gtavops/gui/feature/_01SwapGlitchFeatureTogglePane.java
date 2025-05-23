package club.pineclone.gtavops.gui.feature;

import club.pineclone.gtavops.common.ResourceHolder;
import club.pineclone.gtavops.config.Config;
import club.pineclone.gtavops.gui.component.TriggerModeButton;
import club.pineclone.gtavops.gui.forked.ForkedKeyChooser;
import club.pineclone.gtavops.gui.component.VKeyChooseButton;
import club.pineclone.gtavops.gui.component.VSettingStage;
import club.pineclone.gtavops.gui.forked.ForkedSlider;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.macro.MacroContextHolder;
import club.pineclone.gtavops.macro.trigger.*;
import club.pineclone.gtavops.macro.action.ScheduledAction;
import club.pineclone.gtavops.macro.action.impl.SwapGlitchAction;
import club.pineclone.gtavops.macro.action.decorator.SwapMeleeDecorator;
import club.pineclone.gtavops.macro.action.decorator.SwapRangedDecorator;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.ui.toggle.ToggleSwitch;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/* 切枪偷速 */
public class _01SwapGlitchFeatureTogglePane
        extends FeatureTogglePane
        implements ResourceHolder {

    @Override
    protected String getTitle() {
        return getI18n().swapGlitch.title;
    }

    public _01SwapGlitchFeatureTogglePane() {
        super(new SGFeatureContext(), new SGSettingStage());
    }

    @Override
    public boolean init() {
        return getConfig().swapGlitch.baseSetting.enable;  /* 加载配置 */
    }

    @Override
    public void stop(boolean enabled) {
        getConfig().swapGlitch.baseSetting.enable = enabled;  /* 保存最后状态 */
    }

    private static class SGFeatureContext
            extends FeatureContext
            implements ResourceHolder, MacroContextHolder {

        protected UUID macroId;
        private final Config.SwapGlitch.BaseSetting baseSetting = getConfig().swapGlitch.baseSetting;
        private final Config.SwapGlitch.SwapMeleeSetting swapMeleeSetting = getConfig().swapGlitch.swapMeleeSetting;
        private final Config.SwapGlitch.SwapRangedSetting SwapRangedSetting = getConfig().swapGlitch.swapRangedSetting;

        @Override
        protected void activate() {
            ScheduledAction action = buildAction();
            Trigger trigger = buildTrigger();

            macroId = MACRO_FACTORY.createSimpleMacro(trigger, action);
            MACRO_REGISTRY.install(macroId);  /* 注册宏执行器 */
        }

        /* 构建触发器 */
        private Trigger buildTrigger() {
            Key activatekey = baseSetting.activatekey;  /* 激活热键 */
            TriggerMode mode = baseSetting.activateMethod;  /* 激活模式 切换执行 or 按住执行 */
            return TriggerFactory.simple(new TriggerIdentity(mode, activatekey));  /* 触发器 */
        }

        /* 构建基础动作，并根据配置添加装饰器 */
        private ScheduledAction buildAction() {
            Key weaponWheelHotkey = baseSetting.targetWeaponWheelKey;  /* 武器轮盘热键 */
            long interval = (long) Math.floor(baseSetting.triggerInterval);  /* 偷速间隔 */

            ScheduledAction action = new SwapGlitchAction(weaponWheelHotkey, interval);  /* 基础执行器 */

            boolean swapMelee = swapMeleeSetting.enableSwapMelee;  /* 切入偷速前是否切换近战 */
            if (swapMelee) {  /* 套装饰器 */
                Key swapMeleeHotkey = swapMeleeSetting.meleeWeaponKey;
                long postSwapMeleeDelay = (long) Math.floor(swapMeleeSetting.postSwapMeleeDelay);  /* 偷速间隔 */
                action = new SwapMeleeDecorator(action, swapMeleeHotkey, postSwapMeleeDelay);
            }


            boolean swapRanged = SwapRangedSetting.enableSwapRanged;  /* 切出偷速前是否切换远程 */
            if (swapRanged) {
                Map<Key, Key> sourceToTargetMap = new HashMap<>();

                /* 启用映射1 */
                if (SwapRangedSetting.enableMapping1)
                    sourceToTargetMap.put(SwapRangedSetting.mapping1SourceKey, SwapRangedSetting.mapping1TargetKey);
                /* 启用映射2 */
                if (SwapRangedSetting.enableMapping2)
                    sourceToTargetMap.put(SwapRangedSetting.mapping2SourceKey, SwapRangedSetting.mapping2TargetKey);
                /* 启用映射3 */
                if (SwapRangedSetting.enableMapping3)
                    sourceToTargetMap.put(SwapRangedSetting.mapping3SourceKey, SwapRangedSetting.mapping3TargetKey);
                /* 启用映射4 */
                if (SwapRangedSetting.enableMapping4)
                    sourceToTargetMap.put(SwapRangedSetting.mapping4SourceKey, SwapRangedSetting.mapping4TargetKey);

                action = SwapRangedDecorator.builder()
                        .delegate(action)
                        .swapDefaultRangedWeaponOnEmpty(SwapRangedSetting.swapDefaultRangedWeaponOnEmpty)
                        .defaultRangedWeaponKey(SwapRangedSetting.defaultRangedWeaponKey)
                        .sourceToTargetMap(sourceToTargetMap)
                        .enableClearKey(SwapRangedSetting.enableClearKey)
                        .clearKey(SwapRangedSetting.clearKey)
//                    .blockDuration((long) Math.floor(srSetting.blockDuration))
                        .build();
            }

            return action;
        }

        @Override
        protected void deactivate() {
            MACRO_REGISTRY.uninstall(macroId);  /* 注销宏执行器 */
        }
    }

    private static class SGSettingStage
            extends VSettingStage
            implements ResourceHolder {

        private static final int FLAG_WITH_KEY_AND_MOUSE = ForkedKeyChooser.FLAG_WITH_KEY  | ForkedKeyChooser.FLAG_WITH_MOUSE;
        private static final int FLAG_WITH_ALL = FLAG_WITH_KEY_AND_MOUSE | ForkedKeyChooser.FLAG_WITH_WHEEL_SCROLL;

        private final VKeyChooseButton activateKeyBtn = new VKeyChooseButton(FLAG_WITH_KEY_AND_MOUSE);
        private final VKeyChooseButton weaponWheelKeyBtn = new VKeyChooseButton(FLAG_WITH_ALL);
        private final VKeyChooseButton meleeKeyBtn = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);

        private final ToggleSwitch enableSwapMeleeToggle = new ToggleSwitch();

        private final ExtendedI18n i18n = getI18n();

        private final Config config = getConfig();
        private final Config.SwapGlitch sgConfig = config.swapGlitch;

        private final TriggerModeButton activateMethodBtn = new TriggerModeButton(
                TriggerModeButton.FLAG_WITH_HOLD | TriggerModeButton.FLAG_WITH_TOGGLE);

        private final ForkedSlider triggerIntervalSlider = new ForkedSlider() {{
            setLength(400);
            setRange(10, 100);
        }};

        private final ForkedSlider postSwapMeleeDelaySlider = new ForkedSlider() {{
            setLength(400);
            setRange(10, 200);
        }};

        private final ToggleSwitch enableSwapRangedToggle = new ToggleSwitch();  /* 切出偷速时切换远程武器 */
        private final ToggleSwitch swapDefaultRangedWeaponOnEmptyToggle = new ToggleSwitch();  /* 没有选中任何远程武器时自动切换默认远程武器 */
        private final VKeyChooseButton defaultRangedWeaponKeyBtn = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);  /* 默认远程武器键位 */

        private final ToggleSwitch mapping1Toggle = new ToggleSwitch();  /* 监听远程武器映射1 */
        private final VKeyChooseButton mapping1SourceKeyBtn = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);  /* 映射1主键 */
        private final VKeyChooseButton mapping1TargetKeyBtn = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);  /* 映射1目标键 */

        private final ToggleSwitch mapping2Toggle = new ToggleSwitch();  /* 监听远程武器映射1 */
        private final VKeyChooseButton mapping2SourceKeyBtn = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);  /* 映射1主键 */
        private final VKeyChooseButton mapping2TargetKeyBtn = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);  /* 映射1目标键 */

        private final ToggleSwitch mapping3Toggle = new ToggleSwitch();  /* 监听远程武器映射1 */
        private final VKeyChooseButton mapping3SourceKeyBtn = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);  /* 映射1主键 */
        private final VKeyChooseButton mapping3TargetKeyBtn = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);  /* 映射1目标键 */

        private final ToggleSwitch mapping4Toggle = new ToggleSwitch();  /* 监听远程武器映射4 */
        private final VKeyChooseButton mapping4SourceKeyBtn = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);  /* 映射1主键 */
        private final VKeyChooseButton mapping4TargetKeyBtn = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);  /* 映射1目标键 */

        private final ToggleSwitch swapRangedClearKeyToggle = new ToggleSwitch();  /* 启用屏蔽切换远程武器 */
        private final VKeyChooseButton swapRangedClearKeyBtn = new VKeyChooseButton();  /* 屏蔽切换远程武器键 */

        public SGSettingStage() {
            ExtendedI18n.SwapGlitch sgI18n = i18n.swapGlitch;
            getContent().getChildren().addAll(contentBuilder()
                    /* 基础设置 */
                    .divide(sgI18n.baseSetting.title)
                    .button(sgI18n.baseSetting.activateMethod, activateMethodBtn)
                    .button(sgI18n.baseSetting.targetWeaponWheelKey, weaponWheelKeyBtn)
                    .button(sgI18n.baseSetting.activateKey, activateKeyBtn)
                    .slider(sgI18n.baseSetting.triggerInterval, triggerIntervalSlider)
                    /* 切换近战武器设置 */
                    .divide(sgI18n.swapMeleeSetting.title)
                    .toggle(sgI18n.swapMeleeSetting.enable, enableSwapMeleeToggle)
                    .button(sgI18n.swapMeleeSetting.meleeKey, meleeKeyBtn)
                    .slider(sgI18n.swapMeleeSetting.postSwapMeleeDelay, postSwapMeleeDelaySlider)
                    /* 切换远程武器设置 */
                    .divide(sgI18n.swapRangedSetting.title)
                    .toggle(sgI18n.swapRangedSetting.enable, enableSwapRangedToggle)
                    .buttonToggle(sgI18n.swapRangedSetting.defaultRangedWeaponKey, swapDefaultRangedWeaponOnEmptyToggle, defaultRangedWeaponKeyBtn)
                    .buttonToggle(MessageFormat.format(sgI18n.swapRangedSetting.listenRangedWeaponMapping, 1), mapping1Toggle, mapping1SourceKeyBtn, mapping1TargetKeyBtn)
                    .buttonToggle(MessageFormat.format(sgI18n.swapRangedSetting.listenRangedWeaponMapping, 2), mapping2Toggle, mapping2SourceKeyBtn, mapping2TargetKeyBtn)
                    .buttonToggle(MessageFormat.format(sgI18n.swapRangedSetting.listenRangedWeaponMapping, 3), mapping3Toggle, mapping3SourceKeyBtn, mapping3TargetKeyBtn)
                    .buttonToggle(MessageFormat.format(sgI18n.swapRangedSetting.listenRangedWeaponMapping, 4), mapping4Toggle, mapping4SourceKeyBtn, mapping4TargetKeyBtn)
                    .gap()
                    .toggle(sgI18n.swapRangedSetting.enableClearKey, swapRangedClearKeyToggle)
                    .button(sgI18n.swapRangedSetting.clearKey, swapRangedClearKeyBtn)
//                    .slider(sgI18n.swapRangedSetting.blockDuration, swapRangedBlockDurationSlider)
                    .build()
            );
        }

        @Override
        public String getTitle() {
            return i18n.swapGlitch.title;
        }

        @Override
        public void init() {
            activateKeyBtn.keyProperty().set(sgConfig.baseSetting.activatekey);
            weaponWheelKeyBtn.keyProperty().set(sgConfig.baseSetting.targetWeaponWheelKey);
            meleeKeyBtn.keyProperty().set(sgConfig.swapMeleeSetting.meleeWeaponKey);

            postSwapMeleeDelaySlider.setValue(sgConfig.swapMeleeSetting.postSwapMeleeDelay);

            activateMethodBtn.triggerModeProperty().set(sgConfig.baseSetting.activateMethod);
            triggerIntervalSlider.setValue(sgConfig.baseSetting.triggerInterval);

            enableSwapMeleeToggle.selectedProperty().set(sgConfig.swapMeleeSetting.enableSwapMelee);

            enableSwapRangedToggle.selectedProperty().set(sgConfig.swapRangedSetting.enableSwapRanged);
            swapDefaultRangedWeaponOnEmptyToggle.selectedProperty().set(sgConfig.swapRangedSetting.swapDefaultRangedWeaponOnEmpty);
            defaultRangedWeaponKeyBtn.keyProperty().set(sgConfig.swapRangedSetting.defaultRangedWeaponKey);

            mapping1Toggle.selectedProperty().set(sgConfig.swapRangedSetting.enableMapping1);
            mapping1SourceKeyBtn.keyProperty().set(sgConfig.swapRangedSetting.mapping1SourceKey);
            mapping1TargetKeyBtn.keyProperty().set(sgConfig.swapRangedSetting.mapping1TargetKey);

            mapping2Toggle.selectedProperty().set(sgConfig.swapRangedSetting.enableMapping2);
            mapping2SourceKeyBtn.keyProperty().set(sgConfig.swapRangedSetting.mapping2SourceKey);
            mapping2TargetKeyBtn.keyProperty().set(sgConfig.swapRangedSetting.mapping2TargetKey);

            mapping3Toggle.selectedProperty().set(sgConfig.swapRangedSetting.enableMapping3);
            mapping3SourceKeyBtn.keyProperty().set(sgConfig.swapRangedSetting.mapping3SourceKey);
            mapping3TargetKeyBtn.keyProperty().set(sgConfig.swapRangedSetting.mapping3TargetKey);

            mapping4Toggle.selectedProperty().set(sgConfig.swapRangedSetting.enableMapping4);
            mapping4SourceKeyBtn.keyProperty().set(sgConfig.swapRangedSetting.mapping4SourceKey);
            mapping4TargetKeyBtn.keyProperty().set(sgConfig.swapRangedSetting.mapping4TargetKey);

            swapRangedClearKeyToggle.selectedProperty().set(sgConfig.swapRangedSetting.enableClearKey);
            swapRangedClearKeyBtn.keyProperty().set(sgConfig.swapRangedSetting.clearKey);
        }

        @Override
        public void stop() {
            sgConfig.baseSetting.targetWeaponWheelKey = weaponWheelKeyBtn.keyProperty().get();
            sgConfig.swapMeleeSetting.meleeWeaponKey = meleeKeyBtn.keyProperty().get();

            sgConfig.baseSetting.activatekey = activateKeyBtn.keyProperty().get();
            sgConfig.baseSetting.activateMethod = activateMethodBtn.triggerModeProperty().get();
            sgConfig.baseSetting.triggerInterval = triggerIntervalSlider.valueProperty().get();
            sgConfig.swapMeleeSetting.postSwapMeleeDelay = postSwapMeleeDelaySlider.valueProperty().get();
            sgConfig.swapMeleeSetting.enableSwapMelee = enableSwapMeleeToggle.selectedProperty().get();

            Config.SwapGlitch.SwapRangedSetting srSetting = sgConfig.swapRangedSetting;
            srSetting.enableSwapRanged = enableSwapRangedToggle.selectedProperty().get();
            srSetting.swapDefaultRangedWeaponOnEmpty = swapDefaultRangedWeaponOnEmptyToggle.selectedProperty().get();
            srSetting.defaultRangedWeaponKey = defaultRangedWeaponKeyBtn.keyProperty().get();

            srSetting.enableMapping1 = mapping1Toggle.selectedProperty().get();
            srSetting.mapping1SourceKey = mapping1SourceKeyBtn.keyProperty().get();
            srSetting.mapping1TargetKey = mapping1TargetKeyBtn.keyProperty().get();

            srSetting.enableMapping2 = mapping2Toggle.selectedProperty().get();
            srSetting.mapping2SourceKey = mapping2SourceKeyBtn.keyProperty().get();
            srSetting.mapping2TargetKey = mapping2TargetKeyBtn.keyProperty().get();

            srSetting.enableMapping3 = mapping3Toggle.selectedProperty().get();
            srSetting.mapping3SourceKey = mapping3SourceKeyBtn.keyProperty().get();
            srSetting.mapping3TargetKey = mapping3TargetKeyBtn.keyProperty().get();

            srSetting.enableMapping4 = mapping4Toggle.selectedProperty().get();
            srSetting.mapping4SourceKey = mapping4SourceKeyBtn.keyProperty().get();
            srSetting.mapping4TargetKey = mapping4TargetKeyBtn.keyProperty().get();

            srSetting.enableClearKey = swapRangedClearKeyToggle.selectedProperty().get();
            srSetting.clearKey = swapRangedClearKeyBtn.keyProperty().get();
        }
    }
}
