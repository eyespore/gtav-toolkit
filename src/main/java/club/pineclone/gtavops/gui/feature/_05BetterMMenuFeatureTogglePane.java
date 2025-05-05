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
import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.action.bettermmenu.StartEngineAction;
import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerFactory;
import club.pineclone.gtavops.macro.trigger.TriggerIdentity;
import club.pineclone.gtavops.macro.trigger.TriggerMode;
import io.vproxy.vfx.entity.input.Key;

public class _05BetterMMenuFeatureTogglePane extends FeatureTogglePane {

    ExtendedI18n i18n;
    ExtendedI18n.BetterMMenu bmmI18n;

    Configuration config;
    Configuration.BetterMMenu bmmConfig;

    private SimpleMacro macro;

    public _05BetterMMenuFeatureTogglePane() {
        i18n = I18nHolder.get();
        bmmI18n = i18n.betterMMenu;

        config = ConfigHolder.get();
        bmmConfig = config.betterMMenu;
    }

    @Override
    protected String getTitle() {
        return I18nHolder.get().betterMMenu.title;
    }

    @Override
    protected void activate() {
        Key startEngineKey = bmmConfig.startEngineKey;
        Key menuKey = bmmConfig.menuKey;
        long arrowKeyInterval = (long) (Math.floor(bmmConfig.arrowKeyInterval));
        long enterKeyInterval = (long) (Math.floor(bmmConfig.enterKeyInterval));

        Trigger trigger = TriggerFactory.getTrigger(new TriggerIdentity(startEngineKey, TriggerMode.CLICK));
        Action action = new StartEngineAction(menuKey, arrowKeyInterval, enterKeyInterval);

        macro = new SimpleMacro(trigger, action);
        macro.install();
    }

    @Override
    protected void deactivate() {
        macro.uninstall();
    }

    @Override
    public void init() {
        selectedProperty().set(bmmConfig.enable);
    }

    @Override
    public void stop() {
        bmmConfig.enable = selectedProperty().get();
        selectedProperty().set(false);
    }

    @Override
    public VSettingStage getSettingStage() {
        return new BMMSettingStage();
    }

    private class BMMSettingStage extends VSettingStage {

        private static final int FLAG_WITH_KEY_AND_MOUSE = ForkedKeyChooser.FLAG_WITH_KEY  | ForkedKeyChooser.FLAG_WITH_MOUSE;
        private static final int FLAG_WITH_ALL = FLAG_WITH_KEY_AND_MOUSE | ForkedKeyChooser.FLAG_WITH_WHEEL_SCROLL;

        private final VKeyChooseButton menuKeyBtn = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);
        private final ForkedSlider arrowKeyIntervalSlider = new ForkedSlider() {{
            setLength(200);
            setRange(1, 50);
        }};
        private final ForkedSlider enterKeyIntervalSlider = new ForkedSlider() {{
            setLength(200);
            setRange(1, 50);
        }};
        private final VKeyChooseButton startEngineKeyBtn = new VKeyChooseButton(FLAG_WITH_ALL);

        public BMMSettingStage() {
            getContent().getChildren().addAll(contentBuilder()
                    .button(bmmI18n.menuKey, menuKeyBtn)
                    .slider(bmmI18n.arrowKeyInterval, arrowKeyIntervalSlider)
                    .slider(bmmI18n.enterKeyInterval, enterKeyIntervalSlider)
                    .button(bmmI18n.startEngineKey, startEngineKeyBtn)
                    .build());
        }

        @Override
        public String getTitle() {
            return bmmI18n.title;
        }

        @Override
        public void init() {
            menuKeyBtn.keyProperty().set(bmmConfig.menuKey);
            arrowKeyIntervalSlider.setValue(bmmConfig.arrowKeyInterval);
            enterKeyIntervalSlider.setValue(bmmConfig.enterKeyInterval);
            startEngineKeyBtn.keyProperty().set(bmmConfig.startEngineKey);
        }

        @Override
        public void stop() {
            bmmConfig.menuKey = menuKeyBtn.keyProperty().get();
            bmmConfig.arrowKeyInterval = arrowKeyIntervalSlider.valueProperty().get();
            bmmConfig.enterKeyInterval = enterKeyIntervalSlider.valueProperty().get();
            bmmConfig.startEngineKey = startEngineKeyBtn.keyProperty().get();
        }
    }
}
