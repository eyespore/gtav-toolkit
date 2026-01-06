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

import java.util.UUID;

/* 回血增强 Tab按键 + 滚轮增强 roulette snake */
public class RouletteSnakeToggle extends MacroToggle {

    private UUID macroId;

    public RouletteSnakeToggle(ExtendedI18n i18n) {
        super(i18n);
    }

    @Override
    protected void onFeatureEnable() {
        macroId = MacroRegistry.getInstance().register(MacroConfigLoader.get(), MacroCreationStrategies.ROULETTE_SNAKE_MACRO_CREATION_STRATEGY);
        MacroRegistry.getInstance().launchMacro(macroId);
    }

    @Override
    protected void onFeatureDisable() {
        MacroRegistry.getInstance().terminateMacro(macroId);
    }

    @Override
    protected String getTitle() {
        return i18n.rouletteSnake.title;
    }

    @Override
    protected MacroSettingStage getSetting() {
        return new RSSettingStage(i18n);
    }

    @Override
    public void onUIInit() {
        selectedProperty().set(MacroConfigLoader.get().rouletteSnake.baseSetting.enable);
    }

    @Override
    public void onUIDispose() {
        MacroConfigLoader.get().rouletteSnake.baseSetting.enable = selectedProperty().get();
    }

    private static class RSSettingStage
            extends MacroSettingStage
            implements ResourceHolder {

        private final MacroConfig.RouletteSnake qsConfig = getConfig().rouletteSnake;

        private static final int FLAG_WITH_KEY_AND_MOUSE = ForkedKeyChooser.FLAG_WITH_KEY  | ForkedKeyChooser.FLAG_WITH_MOUSE;
        private static final int FLAG_WITH_ALL = FLAG_WITH_KEY_AND_MOUSE | ForkedKeyChooser.FLAG_WITH_WHEEL_SCROLL;

        private final VKeyChooseButton snakeKeyBtn = new VKeyChooseButton();
        private final VKeyChooseButton activateKeyBtn = new VKeyChooseButton(FLAG_WITH_KEY_AND_MOUSE);
        private final VKeyChooseButton weaponWheelKeyBtn = new VKeyChooseButton(FLAG_WITH_ALL);
        private final ForkedSlider triggerIntervalSlider = new ForkedSlider() {{
            setLength(200);
            setRange(1, 100);
        }};

        public RSSettingStage(ExtendedI18n i18n) {
            super(i18n);
            ExtendedI18n.RouletteSnake qsI18n = i18n.rouletteSnake;
            getContent().getChildren().addAll(contentBuilder()
                            .divide(qsI18n.baseSetting.title)
                            .button(qsI18n.baseSetting.weaponWheelKey, weaponWheelKeyBtn)
                            .button(qsI18n.baseSetting.activateKey, activateKeyBtn)
                            .button(qsI18n.baseSetting.snakeKey, snakeKeyBtn)
                            .slider(qsI18n.baseSetting.triggerInterval, triggerIntervalSlider)
                            .build()
            );
        }

        @Override
        public String getTitle() {
            return i18n.rouletteSnake.title;
        }

        @Override
        public void onVSettingStageInit() {
            super.onVSettingStageInit();
            activateKeyBtn.keyProperty().set(qsConfig.baseSetting.activatekey);
            snakeKeyBtn.keyProperty().set(qsConfig.baseSetting.snakeKey);
            weaponWheelKeyBtn.keyProperty().set(qsConfig.baseSetting.weaponWheel);
            triggerIntervalSlider.setValue(qsConfig.baseSetting.triggerInterval);
        }

        @Override
        public void onVSettingStageExit() {
            qsConfig.baseSetting.activatekey = activateKeyBtn.keyProperty().get();
            qsConfig.baseSetting.snakeKey = snakeKeyBtn.keyProperty().get();
            qsConfig.baseSetting.weaponWheel = weaponWheelKeyBtn.keyProperty().get();
            qsConfig.baseSetting.triggerInterval = triggerIntervalSlider.valueProperty().get();
        }
    }
}
