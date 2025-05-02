package club.pineclone.gtavops.gui.feature;

import club.pineclone.gtavops.config.ConfigHolder;
import club.pineclone.gtavops.config.Configuration;
import club.pineclone.gtavops.gui.component.OptionalButton;
import club.pineclone.gtavops.gui.forked.ForkedKeyChooser;
import club.pineclone.gtavops.gui.component.KeyChooseButton;
import club.pineclone.gtavops.gui.component.SettingStage;
import club.pineclone.gtavops.gui.forked.ForkedSlider;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.i18n.I18nHolder;
import club.pineclone.gtavops.macro.TriggerBindings;
import club.pineclone.gtavops.macro.TriggerFactory;
import club.pineclone.gtavops.macro.TriggerIdentity;
import club.pineclone.gtavops.macro.action.ScheduledAction;
import club.pineclone.gtavops.macro.swapglitch.SwapGlitchAction;
import club.pineclone.gtavops.macro.swapglitch.SwapMeleeDecorator;
import club.pineclone.gtavops.macro.swapglitch.SwapRangedDecorator;
import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerMode;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.toggle.ToggleSwitch;
import io.vproxy.vfx.util.FXUtils;
import javafx.scene.layout.HBox;

/* 切枪偷速 */
public class _01SwapGlitchFeatureTogglePane extends FeatureTogglePane {

    private TriggerBindings bindings;  /* 宏执行器 */

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

        bindings = new TriggerBindings(trigger, action);
        bindings.install();  /* 注册宏执行器 */
    }

    /* 构建触发器 */
    private Trigger buildTrigger() {
        /* 激活热键 */
        Key activateHotkey = sgConfig.activatekey;
        /* 激活模式 切换执行 or 按住执行 */
        TriggerMode mode = sgConfig.activateMethod == 0 ? TriggerMode.TOGGLE : TriggerMode.HOLD;

        /* 触发类型 按键 or 鼠标 or 滚轮触发 */
        return TriggerFactory.getTrigger(new TriggerIdentity(activateHotkey, mode));  /* 触发器 */
    }

    /* 构建基础动作，并根据配置添加装饰器 */
    private ScheduledAction buildAction() {
        Key weaponWheelHotkey = sgConfig.weaponWheel;  /* 武器轮盘热键 */
        long interval = (long) Math.floor(sgConfig.swapInterval);  /* 偷速间隔 */

        ScheduledAction action = new SwapGlitchAction(weaponWheelHotkey, interval);  /* 基础执行器 */

        boolean swapMelee = sgConfig.swapMelee;  /* 切入偷速前是否切换近战 */
        if (swapMelee) {  /* 套装饰器 */
            Key swapMeleeHotkey = sgConfig.meleeWeapon;
            action = new SwapMeleeDecorator(action, swapMeleeHotkey);
        }

        boolean swapRanged = sgConfig.swapRanged;  /* 切出偷速前是否切换远程 */
        if (swapRanged) {
            Key swapRangedHotkey = sgConfig.rangedWeapon;
            action = new SwapRangedDecorator(action, swapRangedHotkey);
        }
        return action;
    }

    @Override
    protected void deactivate() {
        bindings.uninstall();  /* 注销宏执行器 */
    }

    @Override
    public void init() {
        selectedProperty().set(sgConfig.enable);  /* 加载配置 */
    }

    @Override
    public void stop() {
        sgConfig.enable = selectedProperty().get();  /* 保存最后状态 */
        selectedProperty().set(false);  /* 关闭功能 */
    }

    @Override
    public SettingStage getSettingStage() {
        return new SGSettingStage();
    }

    private class SGSettingStage extends SettingStage {

        private static final int FLAG_WITH_KEY_AND_MOUSE = ForkedKeyChooser.FLAG_WITH_KEY  | ForkedKeyChooser.FLAG_WITH_MOUSE;
        private static final int FLAG_WITH_ALL = FLAG_WITH_KEY_AND_MOUSE | ForkedKeyChooser.FLAG_WITH_WHEEL_SCROLL;

        private final KeyChooseButton activateKeyChooseBtn = new KeyChooseButton(FLAG_WITH_KEY_AND_MOUSE);
        private final KeyChooseButton weaponWheelKeyChooseBtn = new KeyChooseButton(FLAG_WITH_ALL);

        private final KeyChooseButton meleeKeyChooseBtn = new KeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);
        private final KeyChooseButton preferredRangedKeyChooseBtn = new KeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);

        private final ToggleSwitch swapMeleeOnActivateToggle = new ToggleSwitch();
        private final ToggleSwitch swapRangedOnDeactivateToggle = new ToggleSwitch();

        private final OptionalButton activateMethodBtn = new OptionalButton() {{
            addOptionalItem(i18n.toggle);
            addOptionalItem(i18n.hold);
        }};

        private final ForkedSlider swapIntervalSlider = new ForkedSlider() {{
            setLength(400);
            setRange(10, 100);
        }};

        public SGSettingStage() {
            super();
            HBox activateKey = createButton(sgI18n.activateKey, activateKeyChooseBtn);
            HBox weaponWheelKey = createButton(sgI18n.weaponWheelKey, weaponWheelKeyChooseBtn);
            HBox meleeKey = createButton(sgI18n.meleeKey, meleeKeyChooseBtn);
            HBox preferredRangedKey = createButton(sgI18n.preferredRangedKey, preferredRangedKeyChooseBtn);

            HBox activateMethod = createButton(sgI18n.activateMethod, activateMethodBtn);
            HBox swapInterval = createSlider(sgI18n.swapInterval, swapIntervalSlider);

            HBox swapMeleeOnActivate = creatToggle(sgI18n.swapMeleeOnActivate, swapMeleeOnActivateToggle);
            HBox swapRangedOnDeactivate = creatToggle(sgI18n.swapRangedOnDeactivate, swapRangedOnDeactivateToggle);

            getContent().getChildren().addAll(
                    new VPadding(10),
                    activateMethod,
                    new VPadding(5),
                    weaponWheelKey,
                    new VPadding(5),
                    activateKey,
                    new VPadding(10),
                    swapMeleeOnActivate,
                    new VPadding(10),
                    meleeKey,
                    new VPadding(18),
                    swapRangedOnDeactivate,
                    new VPadding(10),
                    preferredRangedKey,
                    new VPadding(10),
                    swapInterval,
                    new VPadding(30)
            );

            FXUtils.observeWidth(getStage().getInitialScene().getNode(), getContent(), -30);
        }

        @Override
        public String getTitle() {
            return sgI18n.title;
        }

        @Override
        public void init() {
            activateKeyChooseBtn.keyProperty().set(sgConfig.activatekey);
            weaponWheelKeyChooseBtn.keyProperty().set(sgConfig.weaponWheel);
            meleeKeyChooseBtn.keyProperty().set(sgConfig.meleeWeapon);
            preferredRangedKeyChooseBtn.keyProperty().set(sgConfig.rangedWeapon);

            activateMethodBtn.indexProperty().set(sgConfig.activateMethod);
            swapIntervalSlider.setValue(sgConfig.swapInterval);

            swapMeleeOnActivateToggle.selectedProperty().set(sgConfig.swapMelee);
            swapRangedOnDeactivateToggle.selectedProperty().set(sgConfig.swapRanged);
        }

        @Override
        public void stop() {
            sgConfig.weaponWheel = weaponWheelKeyChooseBtn.keyProperty().get();
            sgConfig.meleeWeapon = meleeKeyChooseBtn.keyProperty().get();
            sgConfig.rangedWeapon = preferredRangedKeyChooseBtn.keyProperty().get();

            sgConfig.activatekey = activateKeyChooseBtn.keyProperty().get();
            sgConfig.activateMethod = activateMethodBtn.indexProperty().get();
            sgConfig.swapInterval = swapIntervalSlider.valueProperty().get();
            sgConfig.swapMelee = swapMeleeOnActivateToggle.selectedProperty().get();
            sgConfig.swapRanged = swapRangedOnDeactivateToggle.selectedProperty().get();
        }
    }
}
