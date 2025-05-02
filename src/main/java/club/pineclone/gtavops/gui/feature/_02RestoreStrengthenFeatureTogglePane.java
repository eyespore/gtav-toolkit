package club.pineclone.gtavops.gui.feature;

import club.pineclone.gtavops.config.ConfigHolder;
import club.pineclone.gtavops.config.Configuration;
import club.pineclone.gtavops.gui.component.KeyChooseButton;
import club.pineclone.gtavops.gui.component.SettingStage;
import club.pineclone.gtavops.gui.forked.ForkedKeyChooser;
import club.pineclone.gtavops.gui.forked.ForkedSlider;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.i18n.I18nHolder;
import club.pineclone.gtavops.macro.TriggerBindings;
import club.pineclone.gtavops.macro.TriggerFactory;
import club.pineclone.gtavops.macro.TriggerIdentity;
import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.restorestrengthen.QuickSnakeAction;
import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerMode;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.util.FXUtils;
import javafx.scene.layout.HBox;

/* 回血增强 Tab按键 + 滚轮增强 */
public class _02RestoreStrengthenFeatureTogglePane extends FeatureTogglePane {

    private TriggerBindings bindings;  /* 宏执行器 */

    Configuration config;
    Configuration.RestoreStrengthen rsConfig;
    ExtendedI18n.RestoreStrengthen rsI18n;

    public _02RestoreStrengthenFeatureTogglePane() {
        this.config = ConfigHolder.get();
        this.rsConfig = config.restoreStrengthen;

        this.rsI18n = I18nHolder.get().restoreStrengthen;
    }

    @Override
    protected String getTitle() {
        return I18nHolder.get().restoreStrengthen.title;
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
        bindings = new TriggerBindings(trigger, action);
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

        private final KeyChooseButton snakeKeyChooseBtn = new KeyChooseButton();
        private final KeyChooseButton activateKeyChooseBtn = new KeyChooseButton(FLAG_WITH_KEY_AND_MOUSE);
        private final KeyChooseButton weaponWheelKeyChooseBtn = new KeyChooseButton(FLAG_WITH_ALL);
        private final ForkedSlider triggerIntervalSlider = new ForkedSlider() {{
            setLength(400);
            setRange(10, 200);
        }};

        public RSSettingStage() {
            super();
            HBox activateKey = createButton(rsI18n.activateKey, activateKeyChooseBtn);
            HBox snakeKey = createButton(rsI18n.snakeKey, snakeKeyChooseBtn);
            HBox weaponWheelKey = createButton(rsI18n.weaponWheelKey, weaponWheelKeyChooseBtn);
            HBox triggerInterval = createSlider(rsI18n.triggerInterval, triggerIntervalSlider);

            getContent().getChildren().addAll(
                    new VPadding(10),
                    weaponWheelKey,
                    new VPadding(5),
                    activateKey,
                    new VPadding(10),
                    snakeKey,
                    new VPadding(18),
                    triggerInterval,
                    new VPadding(30)
            );
            FXUtils.observeWidth(getStage().getInitialScene().getNode(), getContent(), -30);
        }

        @Override
        public String getTitle() {
            return rsI18n.title;
        }

        @Override
        public void init() {
            super.init();
            activateKeyChooseBtn.keyProperty().set(rsConfig.activatekey);
            snakeKeyChooseBtn.keyProperty().set(rsConfig.snakeKey);
            weaponWheelKeyChooseBtn.keyProperty().set(rsConfig.weaponWheel);
            triggerIntervalSlider.setValue(rsConfig.triggerInterval);
        }

        @Override
        public void stop() {
            rsConfig.activatekey = activateKeyChooseBtn.keyProperty().get();
            rsConfig.snakeKey = snakeKeyChooseBtn.keyProperty().get();
            rsConfig.weaponWheel = weaponWheelKeyChooseBtn.keyProperty().get();
            rsConfig.triggerInterval = triggerIntervalSlider.valueProperty().get();
        }
    }
}
