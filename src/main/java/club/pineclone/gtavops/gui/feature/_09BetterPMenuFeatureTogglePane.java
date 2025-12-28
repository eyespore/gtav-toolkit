package club.pineclone.gtavops.gui.feature;

import club.pineclone.gtavops.common.ResourceHolder;
import club.pineclone.gtavops.common.SessionType;
import club.pineclone.gtavops.config.Config;
import club.pineclone.gtavops.gui.component.VCycleButton;
import club.pineclone.gtavops.gui.component.VKeyChooseButton;
import club.pineclone.gtavops.gui.component.VSettingStage;
import club.pineclone.gtavops.gui.forked.ForkedKeyChooser;
import club.pineclone.gtavops.gui.forked.ForkedSlider;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.macro.MacroContextHolder;
import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.action.impl.betterpmenu.JoinABookmarkedJobAction;
import club.pineclone.gtavops.macro.action.impl.betterpmenu.JoinANewSessionAction;
import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerFactory;
import club.pineclone.gtavops.macro.trigger.TriggerIdentity;
import club.pineclone.gtavops.macro.trigger.TriggerMode;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.ui.toggle.ToggleSwitch;

import java.util.List;
import java.util.UUID;

/* 额外功能，例如快速切换战局，快速进入某个夺取 */
public class _09BetterPMenuFeatureTogglePane
        extends FeatureTogglePane
        implements ResourceHolder {

    public _09BetterPMenuFeatureTogglePane() {
        super(new ExtraFunctionFeatureContext(), new BPMSettingStage());
    }

    @Override
    protected String getTitle() {
        return getI18n().betterPMenu.title;
    }

    @Override
    public boolean init() {
        return getConfig().betterPMenu.baseSetting.enable;
    }

    @Override
    public void stop(boolean enabled) {
        getConfig().betterPMenu.baseSetting.enable = enabled;
    }

    private static class ExtraFunctionFeatureContext
            extends FeatureContext
            implements ResourceHolder, MacroContextHolder {

        private UUID joinANewSessionMacroId;  /* 快速加入新战局 */
        private UUID joinABookmarkedJobMacroId;  /* 加入已收藏差事 */

        private final Config config = getConfig();
        private final Config.BetterPMenu bpmconfig = config.betterPMenu;

        @Override
        protected void activate() {
            long mouseScrollInterval = (long) (Math.floor(bpmconfig.baseSetting.mouseScrollInterval));
            long enterKeyInterval = (long) (Math.floor(bpmconfig.baseSetting.enterKeyInterval));
            long timeUtilPMenuLoaded = (long) (Math.floor(bpmconfig.baseSetting.timeUtilPMenuLoaded));

            /* 快速加入新战局 */
            if (bpmconfig.joinANewSession.enable) {
                Key activateKey = bpmconfig.getJoinANewSession().activateKey;
                SessionType sessionType = bpmconfig.joinANewSession.sessionType;

                Trigger trigger = TriggerFactory.simple(TriggerIdentity.of(TriggerMode.CLICK, activateKey));
                Action action = new JoinANewSessionAction(sessionType, mouseScrollInterval, enterKeyInterval, timeUtilPMenuLoaded);

                joinANewSessionMacroId = MACRO_FACTORY.createSimpleMacro(trigger, action);
//                Logger.lowLevelDebug(KeyUtils.toString(activateKey));
                MACRO_REGISTRY.install(joinANewSessionMacroId);
            }

            /* 加入已收藏差事 */
            if (bpmconfig.joinABookmarkedJob.enable) {
                Key activateKey = bpmconfig.getJoinABookmarkedJob().activateKey;
                long timeUtilJobsLoaded = (long) (Math.floor(bpmconfig.joinABookmarkedJob.timeUtilJobsLoaded));

                Trigger trigger = TriggerFactory.simple(TriggerIdentity.of(TriggerMode.CLICK, activateKey));
                Action action = new JoinABookmarkedJobAction(mouseScrollInterval, enterKeyInterval, timeUtilPMenuLoaded, timeUtilJobsLoaded);

                joinABookmarkedJobMacroId = MACRO_FACTORY.createSimpleMacro(trigger, action);
                MACRO_REGISTRY.install(joinABookmarkedJobMacroId);
            }
        }

        @Override
        protected void deactivate() {
            MACRO_REGISTRY.uninstall(joinANewSessionMacroId);
            MACRO_REGISTRY.uninstall(joinABookmarkedJobMacroId);
        }
    }

    private static class BPMSettingStage
            extends VSettingStage
            implements ResourceHolder {

        private final ExtendedI18n i18n = getI18n();
        private final ExtendedI18n.BetterPMenu bpmI18n = i18n.betterPMenu;

        private final Config config = getConfig();
        private final Config.BetterPMenu bpmConfig = config.betterPMenu;

        private static final int FLAG_WITH_KEY_AND_MOUSE = ForkedKeyChooser.FLAG_WITH_KEY  | ForkedKeyChooser.FLAG_WITH_MOUSE;

        private final ForkedSlider mouseScrollIntervalSlider = new ForkedSlider() {{
            setLength(200);
            setRange(10, 200);
        }};
        private final ForkedSlider enterKeyIntervalSlider = new ForkedSlider() {{
            setLength(400);
            setRange(10, 500);
        }};

        private final ForkedSlider timeUtilPMenuLoadedSlider = new ForkedSlider() {{
            setLength(400);
            setRange(10, 3000);
        }};

        /* Join A New Session */
        private final ToggleSwitch enableJoinANewSessionToggle = new ToggleSwitch();
        private final VKeyChooseButton joinANewSessionActivateKey = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);
        private final VCycleButton<SessionType> joinANewSessionTypeCycle = new VCycleButton<>(List.of(SessionType.values()));

        /* Join A Bookmarked Job */
        private final ToggleSwitch enableJoinABookmarkedJobToggle = new ToggleSwitch();
        private final VKeyChooseButton joinABookmarkedJobActivateKey = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);
        private final ForkedSlider timeUtilJobsLoadedSlider = new ForkedSlider() {{
            setLength(400);
            setRange(500, 4000);
        }};

        public BPMSettingStage() {
            getContent().getChildren().addAll(contentBuilder()
                    .divide(bpmI18n.baseSetting.title)
                    .slider(bpmI18n.baseSetting.mouseScrollInterval, mouseScrollIntervalSlider)
                    .slider(bpmI18n.baseSetting.enterKeyInterval, enterKeyIntervalSlider)
                    .slider(bpmI18n.baseSetting.timeUtilPMenuLoaded, timeUtilPMenuLoadedSlider)
                    .divide(bpmI18n.joinANewSession.title)
                    .toggle(bpmI18n.joinANewSession.enable, enableJoinANewSessionToggle)
                    .button(bpmI18n.joinANewSession.activateKey, joinANewSessionActivateKey)
                    .button(bpmI18n.joinANewSession.sessionType, joinANewSessionTypeCycle)
                    .divide(bpmI18n.joinABookmarkedJob.title)
                    .toggle(bpmI18n.joinABookmarkedJob.enable, enableJoinABookmarkedJobToggle)
                    .button(bpmI18n.joinABookmarkedJob.activateKey, joinABookmarkedJobActivateKey)
                    .slider(bpmI18n.joinABookmarkedJob.timeUtilJobsLoaded, timeUtilJobsLoadedSlider)
                    .build());
        }

        @Override
        public String getTitle() {
            return bpmI18n.title;
        }

        @Override
        public void onVSettingStageInit() {
            mouseScrollIntervalSlider.setValue(bpmConfig.baseSetting.mouseScrollInterval);
            enterKeyIntervalSlider.setValue(bpmConfig.baseSetting.enterKeyInterval);
            timeUtilPMenuLoadedSlider.setValue(bpmConfig.baseSetting.timeUtilPMenuLoaded);

            enableJoinANewSessionToggle.selectedProperty().set(bpmConfig.joinANewSession.enable);
            joinANewSessionActivateKey.keyProperty().set(bpmConfig.joinANewSession.activateKey);
            joinANewSessionTypeCycle.itemProperty().set(bpmConfig.joinANewSession.sessionType);

            enableJoinABookmarkedJobToggle.selectedProperty().set(bpmConfig.joinABookmarkedJob.enable);
            joinABookmarkedJobActivateKey.keyProperty().set(bpmConfig.joinABookmarkedJob.activateKey);
            timeUtilJobsLoadedSlider.setValue(bpmConfig.joinABookmarkedJob.timeUtilJobsLoaded);
        }

        @Override
        public void onVSettingStageExit() {
            bpmConfig.baseSetting.mouseScrollInterval = mouseScrollIntervalSlider.valueProperty().get();
            bpmConfig.baseSetting.enterKeyInterval = enterKeyIntervalSlider.valueProperty().get();
            bpmConfig.baseSetting.timeUtilPMenuLoaded = timeUtilPMenuLoadedSlider.valueProperty().get();

            bpmConfig.joinANewSession.enable = enableJoinANewSessionToggle.selectedProperty().get();
            bpmConfig.joinANewSession.activateKey = joinANewSessionActivateKey.keyProperty().get();
            bpmConfig.joinANewSession.sessionType = joinANewSessionTypeCycle.itemProperty().get();

            bpmConfig.joinABookmarkedJob.enable = enableJoinABookmarkedJobToggle.selectedProperty().get();
            bpmConfig.joinABookmarkedJob.activateKey = joinABookmarkedJobActivateKey.keyProperty().get();
            bpmConfig.joinABookmarkedJob.timeUtilJobsLoaded = timeUtilJobsLoadedSlider.valueProperty().get();
        }
    }
}
