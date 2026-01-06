package club.pineclone.gtavops.client.macrotoggle;

import club.pineclone.gtavops.common.ResourceHolder;
import club.pineclone.gtavops.config.MacroConfig;
import club.pineclone.gtavops.config.MacroConfigLoader;
import club.pineclone.gtavops.client.component.VKeyChooseButton;
import club.pineclone.gtavops.client.forked.ForkedKeyChooser;
import club.pineclone.gtavops.client.forked.ForkedSlider;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.macro.MacroCreationStrategies;
import club.pineclone.gtavops.macro.MacroRegistry;
import io.vproxy.vfx.ui.toggle.ToggleSwitch;

import java.text.MessageFormat;
import java.util.UUID;

/* 快速切枪 */
public class QuickSwapToggle extends MacroToggle {

    private UUID macroId;

    public QuickSwapToggle(ExtendedI18n i18n) {
        super(i18n);
    }

    @Override
    protected void onFeatureEnable() {
        macroId = MacroRegistry.getInstance().register(MacroConfigLoader.get(), MacroCreationStrategies.QUICK_SWAP_MACRO_CREATION_STRATEGY);
        MacroRegistry.getInstance().launchMacro(macroId);
    }

    @Override
    protected void onFeatureDisable() {
        MacroRegistry.getInstance().terminateMacro(macroId);
    }

    @Override
    protected String getTitle() {
        return i18n.quickSwap.title;
    }

    @Override
    protected MacroSettingStage getSetting() {
        return new QSSettingStage(i18n);
    }

    @Override
    public void onUIInit() {
        selectedProperty().set(MacroConfigLoader.get().quickSwap.baseSetting.enable);
    }

    @Override
    public void onUIDispose() {
        MacroConfigLoader.get().quickSwap.baseSetting.enable = selectedProperty().get();
    }

    private static class QSSettingStage
            extends MacroSettingStage
            implements ResourceHolder {

        private final MacroConfig config = getConfig();
        private final MacroConfig.QuickSwap qsConfig = config.getQuickSwap();
        private final MacroConfig.SwapGlitch sgConfig = config.getSwapGlitch();
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

        private final ToggleSwitch mapping5Toggle = new ToggleSwitch();  /* 监听远程武器映射5 */
        private final VKeyChooseButton mapping5SourceKeyBtn = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);  /* 映射5主键 */
        private final VKeyChooseButton mapping5TargetKeyBtn = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);  /* 映射5目标键 */

        private final ToggleSwitch enableBlockKeyToggle = new ToggleSwitch();  /* 启用屏蔽键 */
        private final VKeyChooseButton blockKeyBtn = new VKeyChooseButton(FLAG_WITH_ALL);  /* 屏蔽键 */
        private final ForkedSlider blockDurationSlider = new ForkedSlider() {{  /* 屏蔽有效时间 */
            setLength(300);
            setRange(0, 1000);
        }};

        @Override
        public String getTitle() {
            return qsI18n.title;
        }

        public QSSettingStage(ExtendedI18n i18n) {
            super(i18n);
            getContent().getChildren().addAll(contentBuilder()
                    /* 基础设置 */
                    .divide(qsI18n.baseSetting.title)
                    .buttonToggle(MessageFormat.format(qsI18n.baseSetting.quickSwapMapping, 1), mapping1Toggle, mapping1SourceKeyBtn, mapping1TargetKeyBtn)
                    .buttonToggle(MessageFormat.format(qsI18n.baseSetting.quickSwapMapping, 2), mapping2Toggle, mapping2SourceKeyBtn, mapping2TargetKeyBtn)
                    .buttonToggle(MessageFormat.format(qsI18n.baseSetting.quickSwapMapping, 3), mapping3Toggle, mapping3SourceKeyBtn, mapping3TargetKeyBtn)
                    .buttonToggle(MessageFormat.format(qsI18n.baseSetting.quickSwapMapping, 4), mapping4Toggle, mapping4SourceKeyBtn, mapping4TargetKeyBtn)
                    .buttonToggle(MessageFormat.format(qsI18n.baseSetting.quickSwapMapping, 5), mapping5Toggle, mapping5SourceKeyBtn, mapping5TargetKeyBtn)
                    .gap()
                    .toggle(qsI18n.baseSetting.enableBlockKey, enableBlockKeyToggle)
                    .button(qsI18n.baseSetting.blockKey, blockKeyBtn)
                    .slider(qsI18n.baseSetting.blockDuration, blockDurationSlider)
                    .build()
            );
        }

        @Override
        public void onVSettingStageInit() {
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

            mapping5Toggle.selectedProperty().set(qsConfig.baseSetting.enableMapping5);
            mapping5SourceKeyBtn.keyProperty().set(sgConfig.swapRangedSetting.mapping5SourceKey);
            mapping5TargetKeyBtn.keyProperty().set(sgConfig.swapRangedSetting.mapping5TargetKey);

            enableBlockKeyToggle.selectedProperty().set(qsConfig.baseSetting.enableBlockKey);
            blockKeyBtn.keyProperty().set(qsConfig.baseSetting.blockKey);
            blockDurationSlider.setValue(qsConfig.baseSetting.blockDuration);
        }

        @Override
        public void onVSettingStageExit() {
            MacroConfig.SwapGlitch.SwapRangedSetting srSetting = sgConfig.swapRangedSetting;
            MacroConfig.QuickSwap.BaseSetting bSetting = qsConfig.baseSetting;

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

            bSetting.enableMapping5 = mapping5Toggle.selectedProperty().get();
            srSetting.mapping5SourceKey = mapping5SourceKeyBtn.keyProperty().get();
            srSetting.mapping5TargetKey = mapping5TargetKeyBtn.keyProperty().get();

            bSetting.enableBlockKey = enableBlockKeyToggle.selectedProperty().get();
            bSetting.blockKey = blockKeyBtn.keyProperty().get();
            bSetting.blockDuration = blockDurationSlider.valueProperty().get();
        }
    }
}
