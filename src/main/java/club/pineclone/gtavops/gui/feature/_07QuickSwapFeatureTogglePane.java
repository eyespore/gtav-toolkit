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
import club.pineclone.gtavops.macro.action.impl.QuickSwapAction;
import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerFactory;
import club.pineclone.gtavops.macro.trigger.TriggerIdentity;
import club.pineclone.gtavops.macro.trigger.TriggerMode;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.ui.toggle.ToggleSwitch;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/* 快速切枪 */
public class _07QuickSwapFeatureTogglePane
        extends FeatureTogglePane
        implements ResourceHolder {

    public _07QuickSwapFeatureTogglePane() {
        super(new QSFeatureContext(), new QSSettingStage());
    }

    @Override
    protected String getTitle() {
        return getI18n().quickSwap.title;
    }

    @Override
    public boolean init() {
        return getConfig().getQuickSwap().baseSetting.enable;
    }

    @Override
    public void stop(boolean enabled) {
        getConfig().getQuickSwap().baseSetting.enable = enabled;
    }

    private static class QSFeatureContext
            extends FeatureContext
            implements ResourceHolder, MacroContextHolder {

        private UUID macroId;

        private final Config config = getConfig();
        private final Config.QuickSwap qsConfig = config.getQuickSwap();
        private final Config.SwapGlitch sgConfig = config.getSwapGlitch();

        @Override
        protected void activate() {
            Map<Key, Key> sourceToTargetMap = new HashMap<>();
            Config.SwapGlitch.SwapRangedSetting srSetting = sgConfig.swapRangedSetting;
            Config.QuickSwap.BaseSetting bSetting = qsConfig.baseSetting;

            /* 启用映射1 */
            if (bSetting.enableMapping1) sourceToTargetMap.put(srSetting.mapping1SourceKey, srSetting.mapping1TargetKey);
            /* 启用映射2 */
            if (bSetting.enableMapping2) sourceToTargetMap.put(srSetting.mapping2SourceKey, srSetting.mapping2TargetKey);
            /* 启用映射3 */
            if (bSetting.enableMapping3) sourceToTargetMap.put(srSetting.mapping3SourceKey, srSetting.mapping3TargetKey);
            /* 启用映射4 */
            if (bSetting.enableMapping4) sourceToTargetMap.put(srSetting.mapping4SourceKey, srSetting.mapping4TargetKey);

            if (sourceToTargetMap.isEmpty()) return;

            Action action = new QuickSwapAction(
                    sourceToTargetMap,
                    bSetting.enableBlockKey,
                    bSetting.blockKey,
                    (long) Math.floor(bSetting.blockDuration));

            Trigger trigger = TriggerFactory.simple(new TriggerIdentity(TriggerMode.CLICK, sourceToTargetMap.keySet()));
            macroId = MACRO_FACTORY.createSimpleMacro(trigger, action);
            MACRO_REGISTRY.install(macroId);
        }

        @Override
        protected void deactivate() {
            MACRO_REGISTRY.uninstall(macroId);
        }

    }

    private static class QSSettingStage
            extends VSettingStage
            implements ResourceHolder {

        private final Config config = getConfig();
        private final Config.QuickSwap qsConfig = config.getQuickSwap();
        private final Config.SwapGlitch sgConfig = config.getSwapGlitch();
        private final ExtendedI18n i18n = getI18n();
        private final ExtendedI18n.QuickSwap qsI18n = i18n.getQuickSwap();

        private static final int FLAG_WITH_KEY_AND_MOUSE = ForkedKeyChooser.FLAG_WITH_KEY  | ForkedKeyChooser.FLAG_WITH_MOUSE;
        private static final int FLAG_WITH_ALL = FLAG_WITH_KEY_AND_MOUSE | ForkedKeyChooser.FLAG_WITH_WHEEL_SCROLL;

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

        private final ToggleSwitch enableBlockKeyToggle = new ToggleSwitch();  /* 启用屏蔽键 */
        private final VKeyChooseButton blockKeyBtn = new VKeyChooseButton(FLAG_WITH_ALL);  /* 屏蔽键 */
        private final ForkedSlider blockDurationSlider = new ForkedSlider() {{  /* 屏蔽有效时间 */
            setLength(400);
            setRange(0, 1000);
        }};

        @Override
        public String getTitle() {
            return qsI18n.title;
        }

        public QSSettingStage() {
            super();
            getContent().getChildren().addAll(contentBuilder()
                    /* 基础设置 */
                    .divide(qsI18n.baseSetting.title)
                    .buttonToggle(MessageFormat.format(qsI18n.baseSetting.quickSwapMapping, 1), mapping1Toggle, mapping1SourceKeyBtn, mapping1TargetKeyBtn)
                    .buttonToggle(MessageFormat.format(qsI18n.baseSetting.quickSwapMapping, 2), mapping2Toggle, mapping2SourceKeyBtn, mapping2TargetKeyBtn)
                    .buttonToggle(MessageFormat.format(qsI18n.baseSetting.quickSwapMapping, 3), mapping3Toggle, mapping3SourceKeyBtn, mapping3TargetKeyBtn)
                    .buttonToggle(MessageFormat.format(qsI18n.baseSetting.quickSwapMapping, 4), mapping4Toggle, mapping4SourceKeyBtn, mapping4TargetKeyBtn)
                    .gap()
                    .toggle(qsI18n.baseSetting.enableBlockKey, enableBlockKeyToggle)
                    .button(qsI18n.baseSetting.blockKey, blockKeyBtn)
                    .slider(qsI18n.baseSetting.blockDuration, blockDurationSlider)
                    .build()
            );
        }

        @Override
        public void init() {
            mapping1Toggle.selectedProperty().set(qsConfig.baseSetting.enableMapping1);
            mapping1SourceKeyBtn.keyProperty().set(sgConfig.swapRangedSetting.mapping1SourceKey);
            mapping1TargetKeyBtn.keyProperty().set(sgConfig.swapRangedSetting.mapping1TargetKey);

            mapping2Toggle.selectedProperty().set(qsConfig.baseSetting.enableMapping2);
            mapping2SourceKeyBtn.keyProperty().set(sgConfig.swapRangedSetting.mapping2SourceKey);
            mapping2TargetKeyBtn.keyProperty().set(sgConfig.swapRangedSetting.mapping2TargetKey);

            mapping3Toggle.selectedProperty().set(qsConfig.baseSetting.enableMapping3);
            mapping3SourceKeyBtn.keyProperty().set(sgConfig.swapRangedSetting.mapping3SourceKey);
            mapping3TargetKeyBtn.keyProperty().set(sgConfig.swapRangedSetting.mapping3TargetKey);

            mapping4Toggle.selectedProperty().set(qsConfig.baseSetting.enableMapping4);
            mapping4SourceKeyBtn.keyProperty().set(sgConfig.swapRangedSetting.mapping4SourceKey);
            mapping4TargetKeyBtn.keyProperty().set(sgConfig.swapRangedSetting.mapping4TargetKey);

            enableBlockKeyToggle.selectedProperty().set(qsConfig.baseSetting.enableBlockKey);
            blockKeyBtn.keyProperty().set(qsConfig.baseSetting.blockKey);
            blockDurationSlider.setValue(qsConfig.baseSetting.blockDuration);
        }

        @Override
        public void stop() {
            Config.SwapGlitch.SwapRangedSetting srSetting = sgConfig.swapRangedSetting;
            Config.QuickSwap.BaseSetting bSetting = qsConfig.baseSetting;

            bSetting.enableMapping1 = mapping1Toggle.selectedProperty().get();
            srSetting.mapping1SourceKey = mapping1SourceKeyBtn.keyProperty().get();
            srSetting.mapping1TargetKey = mapping1TargetKeyBtn.keyProperty().get();

            bSetting.enableMapping2 = mapping2Toggle.selectedProperty().get();
            srSetting.mapping2SourceKey = mapping2SourceKeyBtn.keyProperty().get();
            srSetting.mapping2TargetKey = mapping2TargetKeyBtn.keyProperty().get();

            bSetting.enableMapping3 = mapping3Toggle.selectedProperty().get();
            srSetting.mapping3SourceKey = mapping3SourceKeyBtn.keyProperty().get();
            srSetting.mapping3TargetKey = mapping3TargetKeyBtn.keyProperty().get();

            bSetting.enableMapping4 = mapping4Toggle.selectedProperty().get();
            srSetting.mapping4SourceKey = mapping4SourceKeyBtn.keyProperty().get();
            srSetting.mapping4TargetKey = mapping4TargetKeyBtn.keyProperty().get();

            bSetting.enableBlockKey = enableBlockKeyToggle.selectedProperty().get();
            bSetting.blockKey = blockKeyBtn.keyProperty().get();
            bSetting.blockDuration = blockDurationSlider.valueProperty().get();
        }
    }
}
