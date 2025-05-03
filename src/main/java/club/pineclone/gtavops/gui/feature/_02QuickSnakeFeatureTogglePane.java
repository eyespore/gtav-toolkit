package club.pineclone.gtavops.gui.feature;

import club.pineclone.gtavops.config.ConfigHolder;
import club.pineclone.gtavops.config.Configuration;
import club.pineclone.gtavops.gui.component.KeyChooseButton;
import club.pineclone.gtavops.gui.component.SettingStage;
import club.pineclone.gtavops.gui.forked.ForkedKeyChooser;
import club.pineclone.gtavops.gui.forked.ForkedSlider;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.i18n.I18nHolder;
import club.pineclone.gtavops.macro.SimpleMacro;
import club.pineclone.gtavops.macro.trigger.TriggerFactory;
import club.pineclone.gtavops.macro.trigger.TriggerIdentity;
import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.action.quickSnake.QuickSnakeAction;
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
        Key snakeKey = rsConfig.snakeKey;
        Key activatekey = rsConfig.activatekey;
        Key weaponWheelKey = rsConfig.weaponWheel;
        long triggerInterval = (long) Math.floor(rsConfig.triggerInterval);

        TriggerIdentity identity1 = new TriggerIdentity(activatekey, TriggerMode.HOLD);
        TriggerIdentity identity2 = new TriggerIdentity(weaponWheelKey, TriggerMode.HOLD);
        Trigger trigger = TriggerFactory.getTrigger(identity1, identity2);

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
        selectedProperty().set(rsConfig.enable);
    }

    @Override
    public void stop() {
        rsConfig.enable = selectedProperty().get();  /* 保存最后状态 */
        selectedProperty().set(false);  /* 关闭功能 */
    }

    @Override
    public SettingStage getSettingStage() {
        return new RSSettingStage();
    }

    private class RSSettingStage extends SettingStage {

        private static final int FLAG_WITH_KEY_AND_MOUSE = ForkedKeyChooser.FLAG_WITH_KEY  | ForkedKeyChooser.FLAG_WITH_MOUSE;
        private static final int FLAG_WITH_ALL = FLAG_WITH_KEY_AND_MOUSE | ForkedKeyChooser.FLAG_WITH_WHEEL_SCROLL;

        private final KeyChooseButton snakeKeyBtn = new KeyChooseButton();
        private final KeyChooseButton activateKeyBtn = new KeyChooseButton(FLAG_WITH_KEY_AND_MOUSE);
        private final KeyChooseButton weaponWheelKeyBtn = new KeyChooseButton(FLAG_WITH_ALL);
        private final ForkedSlider triggerIntervalSlider = new ForkedSlider() {{
            setLength(400);
            setRange(10, 200);
        }};

        public RSSettingStage() {
            super();
            getContent().getChildren().addAll(contentBuilder()
                            .button(rsI18n.weaponWheelKey, weaponWheelKeyBtn)
                            .button(rsI18n.activateKey, activateKeyBtn)
                            .button(rsI18n.snakeKey, snakeKeyBtn)
                            .slider(rsI18n.triggerInterval, triggerIntervalSlider)
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
            activateKeyBtn.keyProperty().set(rsConfig.activatekey);
            snakeKeyBtn.keyProperty().set(rsConfig.snakeKey);
            weaponWheelKeyBtn.keyProperty().set(rsConfig.weaponWheel);
            triggerIntervalSlider.setValue(rsConfig.triggerInterval);
        }

        @Override
        public void stop() {
            rsConfig.activatekey = activateKeyBtn.keyProperty().get();
            rsConfig.snakeKey = snakeKeyBtn.keyProperty().get();
            rsConfig.weaponWheel = weaponWheelKeyBtn.keyProperty().get();
            rsConfig.triggerInterval = triggerIntervalSlider.valueProperty().get();
        }
    }
}
