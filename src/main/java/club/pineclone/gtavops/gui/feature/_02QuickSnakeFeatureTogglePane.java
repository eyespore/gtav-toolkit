package club.pineclone.gtavops.gui.feature;

import club.pineclone.gtavops.common.ResourceHolder;
import club.pineclone.gtavops.config.Config;
import club.pineclone.gtavops.gui.component.VKeyChooseButton;
import club.pineclone.gtavops.gui.component.VSettingStage;
import club.pineclone.gtavops.gui.forked.ForkedKeyChooser;
import club.pineclone.gtavops.gui.forked.ForkedSlider;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.macro.MacroContextHolder;
import club.pineclone.gtavops.macro.trigger.TriggerFactory;
import club.pineclone.gtavops.macro.trigger.TriggerIdentity;
import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.action.impl.QuickSnakeAction;
import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerMode;
import io.vproxy.vfx.entity.input.Key;

import java.util.UUID;

/* 回血增强 Tab按键 + 滚轮增强 */
public class _02QuickSnakeFeatureTogglePane
        extends FeatureTogglePane
        implements ResourceHolder {

    public _02QuickSnakeFeatureTogglePane() {
        super(new QSFeatureContext(), new QSSettingStage());
    }

    @Override
    protected String getTitle() {
        return getI18n().quickSnake.title;
    }

    @Override
    public boolean init() {
        return getConfig().quickSnake.baseSetting.enable;
    }

    @Override
    public void stop(boolean enabled) {
        getConfig().quickSnake.baseSetting.enable = enabled;  /* 保存最后状态 */
    }

    private static class QSFeatureContext
            extends FeatureContext
            implements ResourceHolder, MacroContextHolder {

        private UUID macroId;
        private final Config.QuickSnake rsConfig = getConfig().quickSnake;

        @Override
        protected void activate() {
            Key snakeKey = rsConfig.baseSetting.snakeKey;
            Key activatekey = rsConfig.baseSetting.activatekey;
            Key weaponWheelKey = rsConfig.baseSetting.weaponWheel;
            long triggerInterval = (long) Math.floor(rsConfig.baseSetting.triggerInterval);

            TriggerIdentity identity1 = new TriggerIdentity(TriggerMode.HOLD, activatekey);
            TriggerIdentity identity2 = new TriggerIdentity(TriggerMode.HOLD, weaponWheelKey);
            Trigger trigger = TriggerFactory.composite(identity1, identity2);

            Action action = new QuickSnakeAction(triggerInterval, snakeKey);
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

        private final Config.QuickSnake qsConfig = getConfig().quickSnake;

        private static final int FLAG_WITH_KEY_AND_MOUSE = ForkedKeyChooser.FLAG_WITH_KEY  | ForkedKeyChooser.FLAG_WITH_MOUSE;
        private static final int FLAG_WITH_ALL = FLAG_WITH_KEY_AND_MOUSE | ForkedKeyChooser.FLAG_WITH_WHEEL_SCROLL;

        private final VKeyChooseButton snakeKeyBtn = new VKeyChooseButton();
        private final VKeyChooseButton activateKeyBtn = new VKeyChooseButton(FLAG_WITH_KEY_AND_MOUSE);
        private final VKeyChooseButton weaponWheelKeyBtn = new VKeyChooseButton(FLAG_WITH_ALL);
        private final ForkedSlider triggerIntervalSlider = new ForkedSlider() {{
            setLength(200);
            setRange(1, 100);
        }};

        public QSSettingStage() {
            super();
            ExtendedI18n.QuickSnake qsI18n = getI18n().quickSnake;
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
            return getI18n().quickSnake.title;
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
