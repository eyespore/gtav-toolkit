package club.pineclone.gtavops.gui.feature;

import club.pineclone.gtavops.config.ConfigHolder;
import club.pineclone.gtavops.config.Configuration;
import club.pineclone.gtavops.gui.component.VOptionalButton;
import club.pineclone.gtavops.gui.forked.ForkedKeyChooser;
import club.pineclone.gtavops.gui.component.VKeyChooseButton;
import club.pineclone.gtavops.gui.component.VSettingStage;
import club.pineclone.gtavops.gui.forked.ForkedSlider;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.i18n.I18nHolder;
import club.pineclone.gtavops.macro.SimpleMacro;
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

/* 切枪偷速 */
public class _01SwapGlitchFeatureTogglePane extends FeatureTogglePane {

    private SimpleMacro macro;  /* 宏执行器 */

    Configuration.SwapGlitch sgConfig;

    ExtendedI18n i18n;
    ExtendedI18n.SwapGlitch sgI18n;  /* 切枪偷速本地化 */

    @Override
    protected String getTitle() {
        return I18nHolder.get().swapGlitch.title;
    }

    public _01SwapGlitchFeatureTogglePane() {
        sgConfig = ConfigHolder.get().swapGlitch;

        i18n = I18nHolder.get();
        sgI18n = i18n.swapGlitch;  /* 切枪偷速本地化 */
    }

    @Override
    protected void activate() {
        ScheduledAction action = buildAction();
        Trigger trigger = buildTrigger();
        macro = new SimpleMacro(trigger, action);
        macro.install();  /* 注册宏执行器 */
    }

    /* 构建触发器 */
    private Trigger buildTrigger() {
        /* 激活热键 */
        Key activatekey = sgConfig.baseSetting.activatekey;
        /* 激活模式 切换执行 or 按住执行 */
        TriggerMode mode = sgConfig.baseSetting.activateMethod == 0 ? TriggerMode.HOLD : TriggerMode.TOGGLE;

        /* 触发类型 按键 or 鼠标 or 滚轮触发 */
        return TriggerFactory.simple(new TriggerIdentity(mode, activatekey));  /* 触发器 */
    }

    /* 构建基础动作，并根据配置添加装饰器 */
    private ScheduledAction buildAction() {
        Key weaponWheelHotkey = sgConfig.baseSetting.targetWeaponWheelKey;  /* 武器轮盘热键 */
        long interval = (long) Math.floor(sgConfig.baseSetting.triggerInterval);  /* 偷速间隔 */

        ScheduledAction action = new SwapGlitchAction(weaponWheelHotkey, interval);  /* 基础执行器 */

        boolean swapMelee = sgConfig.swapMeleeSetting.enableSwapMelee;  /* 切入偷速前是否切换近战 */
        if (swapMelee) {  /* 套装饰器 */
            Key swapMeleeHotkey = sgConfig.swapMeleeSetting.meleeWeaponKey;
            long postSwapMeleeDelay = (long) Math.floor(sgConfig.swapMeleeSetting.postSwapMeleeDelay);  /* 偷速间隔 */
            action = new SwapMeleeDecorator(action, swapMeleeHotkey, postSwapMeleeDelay);
        }

        Configuration.SwapGlitch.SwapRangedSetting srSetting = sgConfig.swapRangedSetting;
        boolean swapRanged = srSetting.enableSwapRanged;  /* 切出偷速前是否切换远程 */
        if (swapRanged) {
            Map<Key, Key> sourceToTargetMap = new HashMap<>();

            /* 启用映射1 */
            if (srSetting.enableMapping1) sourceToTargetMap.put(srSetting.mapping1SourceKey, srSetting.mapping1TargetKey);
            /* 启用映射2 */
            if (srSetting.enableMapping2) sourceToTargetMap.put(srSetting.mapping2SourceKey, srSetting.mapping2TargetKey);
            /* 启用映射3 */
            if (srSetting.enableMapping3) sourceToTargetMap.put(srSetting.mapping3SourceKey, srSetting.mapping3TargetKey);
            /* 启用映射4 */
            if (srSetting.enableMapping4) sourceToTargetMap.put(srSetting.mapping4SourceKey, srSetting.mapping4TargetKey);

            action = SwapRangedDecorator.builder()
                    .delegate(action)
                    .swapDefaultRangedWeaponOnEmpty(srSetting.swapDefaultRangedWeaponOnEmpty)
                    .defaultRangedWeaponKey(srSetting.defaultRangedWeaponKey)
                    .sourceToTargetMap(sourceToTargetMap)
                    .enableBlockKey(srSetting.enableBlockKey)
                    .blockKey(srSetting.blockKey)
                    .blockDuration((long) Math.floor(srSetting.blockDuration))
                    .build();
        }

        return action;
    }

    @Override
    protected void deactivate() {
        macro.uninstall();  /* 注销宏执行器 */
    }

    @Override
    public void init() {
        selectedProperty().set(sgConfig.baseSetting.enable);  /* 加载配置 */
    }

    @Override
    public void stop() {
        sgConfig.baseSetting.enable = selectedProperty().get();  /* 保存最后状态 */
        selectedProperty().set(false);  /* 关闭功能 */
    }

    @Override
    public VSettingStage getSettingStage() {
        return new SGSettingStage();
    }

    private class SGSettingStage extends VSettingStage {

        private static final int FLAG_WITH_KEY_AND_MOUSE = ForkedKeyChooser.FLAG_WITH_KEY  | ForkedKeyChooser.FLAG_WITH_MOUSE;
        private static final int FLAG_WITH_ALL = FLAG_WITH_KEY_AND_MOUSE | ForkedKeyChooser.FLAG_WITH_WHEEL_SCROLL;

        private final VKeyChooseButton activateKeyBtn = new VKeyChooseButton(FLAG_WITH_KEY_AND_MOUSE);
        private final VKeyChooseButton weaponWheelKeyBtn = new VKeyChooseButton(FLAG_WITH_ALL);
        private final VKeyChooseButton meleeKeyBtn = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);

        private final ToggleSwitch enableSwapMeleeToggle = new ToggleSwitch();


        private final VOptionalButton activateMethodBtn = new VOptionalButton() {{
            addOptionalItem(i18n.hold);
            addOptionalItem(i18n.toggle);
        }};

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

        private final ToggleSwitch swapRangedBlockKeyToggle = new ToggleSwitch();  /* 启用屏蔽切换远程武器 */
        private final VKeyChooseButton swapRangedBlockKeyBtn = new VKeyChooseButton();  /* 屏蔽切换远程武器键 */
        private final ForkedSlider swapRangedBlockDurationSlider = new ForkedSlider() {{  /* 屏蔽切换远程武器有效时间 */
            setLength(400);
            setRange(0, 1000);
        }};

        public SGSettingStage() {
            super();
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
                    .toggle(sgI18n.swapRangedSetting.enableBlockKey, swapRangedBlockKeyToggle)
                    .button(sgI18n.swapRangedSetting.blockKey, swapRangedBlockKeyBtn)
                    .slider(sgI18n.swapRangedSetting.blockDuration, swapRangedBlockDurationSlider)
                    .build()
            );
        }

        @Override
        public String getTitle() {
            return sgI18n.title;
        }

        @Override
        public void init() {
            activateKeyBtn.keyProperty().set(sgConfig.baseSetting.activatekey);
            weaponWheelKeyBtn.keyProperty().set(sgConfig.baseSetting.targetWeaponWheelKey);
            meleeKeyBtn.keyProperty().set(sgConfig.swapMeleeSetting.meleeWeaponKey);

            postSwapMeleeDelaySlider.setValue(sgConfig.swapMeleeSetting.postSwapMeleeDelay);

            activateMethodBtn.indexProperty().set(sgConfig.baseSetting.activateMethod);
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

            swapRangedBlockKeyToggle.selectedProperty().set(sgConfig.swapRangedSetting.enableBlockKey);
            swapRangedBlockKeyBtn.keyProperty().set(sgConfig.swapRangedSetting.blockKey);
            swapRangedBlockDurationSlider.setValue(sgConfig.swapRangedSetting.blockDuration);
        }

        @Override
        public void stop() {
            sgConfig.baseSetting.targetWeaponWheelKey = weaponWheelKeyBtn.keyProperty().get();
            sgConfig.swapMeleeSetting.meleeWeaponKey = meleeKeyBtn.keyProperty().get();

            sgConfig.baseSetting.activatekey = activateKeyBtn.keyProperty().get();
            sgConfig.baseSetting.activateMethod = activateMethodBtn.indexProperty().get();
            sgConfig.baseSetting.triggerInterval = triggerIntervalSlider.valueProperty().get();
            sgConfig.swapMeleeSetting.postSwapMeleeDelay = postSwapMeleeDelaySlider.valueProperty().get();
            sgConfig.swapMeleeSetting.enableSwapMelee = enableSwapMeleeToggle.selectedProperty().get();

            Configuration.SwapGlitch.SwapRangedSetting srSetting = sgConfig.swapRangedSetting;
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

            srSetting.enableBlockKey = swapRangedBlockKeyToggle.selectedProperty().get();
            srSetting.blockKey = swapRangedBlockKeyBtn.keyProperty().get();
            srSetting.blockDuration = swapRangedBlockDurationSlider.valueProperty().get();
        }
    }
}
