package club.pineclone.gtavops.gui.feature;

import club.pineclone.gtavops.config.ConfigHolder;
import club.pineclone.gtavops.config.Configuration;
import club.pineclone.gtavops.gui.component.VKeyChooseButton;
import club.pineclone.gtavops.gui.component.VSettingStage;
import club.pineclone.gtavops.gui.forked.ForkedKeyChooser;
import club.pineclone.gtavops.gui.forked.ForkedSlider;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.i18n.I18nHolder;
import club.pineclone.gtavops.macro.SimpleMacro;
import club.pineclone.gtavops.macro.trigger.TriggerFactory;
import club.pineclone.gtavops.macro.trigger.TriggerIdentity;
import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.action.impl.QuickSnakeAction;
import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerMode;
import io.vproxy.vfx.entity.input.Key;

/* 回血增强 Tab按键 + 滚轮增强 */
public class _02QuickSnakeFeatureTogglePane extends FeatureTogglePane {

    private SimpleMacro bindings;  /* 宏执行器 */

    Configuration config;
    Configuration.QuickSnake rsConfig;
    ExtendedI18n.QuickSnake rsI18n;

    public _02QuickSnakeFeatureTogglePane() {
        this.config = ConfigHolder.get();
        this.rsConfig = config.quickSnake;

        this.rsI18n = I18nHolder.get().quickSnake;
    }

    @Override
    protected String getTitle() {
        return I18nHolder.get().quickSnake.title;
    }

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
        bindings = new SimpleMacro(trigger, action);
        bindings.install();
    }

    @Override
    protected void deactivate() {
        bindings.uninstall();
    }

    @Override
    public void init() {
        selectedProperty().set(rsConfig.baseSetting.enable);
    }

    @Override
    public void stop() {
        rsConfig.baseSetting.enable = selectedProperty().get();  /* 保存最后状态 */
        selectedProperty().set(false);  /* 关闭功能 */
    }

    @Override
    public VSettingStage getSettingStage() {
        return new RSSettingStage();
    }

    private class RSSettingStage extends VSettingStage {

        private static final int FLAG_WITH_KEY_AND_MOUSE = ForkedKeyChooser.FLAG_WITH_KEY  | ForkedKeyChooser.FLAG_WITH_MOUSE;
        private static final int FLAG_WITH_ALL = FLAG_WITH_KEY_AND_MOUSE | ForkedKeyChooser.FLAG_WITH_WHEEL_SCROLL;

        private final VKeyChooseButton snakeKeyBtn = new VKeyChooseButton();
        private final VKeyChooseButton activateKeyBtn = new VKeyChooseButton(FLAG_WITH_KEY_AND_MOUSE);
        private final VKeyChooseButton weaponWheelKeyBtn = new VKeyChooseButton(FLAG_WITH_ALL);
        private final ForkedSlider triggerIntervalSlider = new ForkedSlider() {{
            setLength(200);
            setRange(1, 100);
        }};

        public RSSettingStage() {
            super();
            getContent().getChildren().addAll(contentBuilder()
                            .divide(rsI18n.baseSetting.title)
                            .button(rsI18n.baseSetting.weaponWheelKey, weaponWheelKeyBtn)
                            .button(rsI18n.baseSetting.activateKey, activateKeyBtn)
                            .button(rsI18n.baseSetting.snakeKey, snakeKeyBtn)
                            .slider(rsI18n.baseSetting.triggerInterval, triggerIntervalSlider)
                            .build()
            );
        }

        @Override
        public String getTitle() {
            return rsI18n.title;
        }

        @Override
        public void init() {
            super.init();
            activateKeyBtn.keyProperty().set(rsConfig.baseSetting.activatekey);
            snakeKeyBtn.keyProperty().set(rsConfig.baseSetting.snakeKey);
            weaponWheelKeyBtn.keyProperty().set(rsConfig.baseSetting.weaponWheel);
            triggerIntervalSlider.setValue(rsConfig.baseSetting.triggerInterval);
        }

        @Override
        public void stop() {
            rsConfig.baseSetting.activatekey = activateKeyBtn.keyProperty().get();
            rsConfig.baseSetting.snakeKey = snakeKeyBtn.keyProperty().get();
            rsConfig.baseSetting.weaponWheel = weaponWheelKeyBtn.keyProperty().get();
            rsConfig.baseSetting.triggerInterval = triggerIntervalSlider.valueProperty().get();
        }
    }
}
