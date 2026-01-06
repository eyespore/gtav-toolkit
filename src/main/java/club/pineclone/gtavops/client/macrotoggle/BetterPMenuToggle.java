package club.pineclone.gtavops.client.macrotoggle;

import club.pineclone.gtavops.common.ResourceHolder;
import club.pineclone.gtavops.common.SessionType;
import club.pineclone.gtavops.config.MacroConfig;
import club.pineclone.gtavops.config.MacroConfigLoader;
import club.pineclone.gtavops.client.component.VOptionButton;
import club.pineclone.gtavops.client.component.VKeyChooseButton;
import club.pineclone.gtavops.client.forked.ForkedKeyChooser;
import club.pineclone.gtavops.client.forked.ForkedSlider;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.macro.MacroCreationStrategies;
import club.pineclone.gtavops.macro.MacroRegistry;
import io.vproxy.vfx.ui.toggle.ToggleSwitch;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/* 额外功能，例如快速切换战局，快速进入某个夺取 */
public class BetterPMenuToggle extends MacroToggle {

    private UUID joinANewSessionMacroId;  /* 快速加入新战局 */
    private UUID joinABookmarkedJobMacroId;  /* 加入已收藏差事 */

    public BetterPMenuToggle(ExtendedI18n i18n) {
        super(i18n);
    }



    @Override
    protected void onFeatureEnable() {
        MacroConfig.BetterPMenu bpmconfig = MacroConfigLoader.get().betterPMenu;
        /* 快速加入新战局 */
        if (bpmconfig.joinANewSession.enable) {
            joinANewSessionMacroId = MacroRegistry.getInstance().register(MacroConfigLoader.get(), MacroCreationStrategies.JOIN_A_NEW_SESSION_MACRO_CREATION_STRATEGY);
            MacroRegistry.getInstance().launchMacro(joinANewSessionMacroId);
        }
        /* 加入已收藏差事 */
        if (bpmconfig.joinABookmarkedJob.enable) {
            joinABookmarkedJobMacroId = MacroRegistry.getInstance().register(MacroConfigLoader.get(), MacroCreationStrategies.JOIN_A_BOOKMARKED_JOB_MACRO_CREATION_STRATEGY);;
            MacroRegistry.getInstance().launchMacro(joinABookmarkedJobMacroId);
        }
    }

    @Override
    protected void onFeatureDisable() {
        MacroRegistry.getInstance().terminateMacro(joinANewSessionMacroId);
        MacroRegistry.getInstance().terminateMacro(joinABookmarkedJobMacroId);
    }

    @Override
    protected String getTitle() {
        return i18n.betterPMenu.title;
    }

    @Override
    protected MacroSettingStage getSetting() {
        return new BPMSettingStage(i18n);
    }

    @Override
    public void onUIInit() {
        selectedProperty().set(MacroConfigLoader.get().betterPMenu.baseSetting.enable);
    }

    @Override
    public void onUIDispose() {
        MacroConfigLoader.get().betterPMenu.baseSetting.enable = selectedProperty().get();
    }

    private static class BPMSettingStage
            extends MacroSettingStage
            implements ResourceHolder {

        private final ExtendedI18n.BetterPMenu bpmI18n = i18n.betterPMenu;

        private final MacroConfig config = getConfig();
        private final MacroConfig.BetterPMenu bpmConfig = config.betterPMenu;

        private final ForkedSlider mouseScrollIntervalSlider = new ForkedSlider() {{
            setLength(200);
            setRange(10, 200);
        }};
        private final ForkedSlider enterKeyIntervalSlider = new ForkedSlider() {{
            setLength(300);
            setRange(10, 500);
        }};

        private final ForkedSlider timeUtilPMenuLoadedSlider = new ForkedSlider() {{
            setLength(300);
            setRange(10, 3000);
        }};

        /* Join A New Session */
        private final ToggleSwitch enableJoinANewSessionToggle = new ToggleSwitch();
        private final VKeyChooseButton joinANewSessionActivateKey = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);

        private final Map<SessionType, String> sessionTypeI18nMap = Map.of(
                SessionType.PUBLIC_SESSION, i18n.inGame.publicSession,
                SessionType.INVITE_ONLY_SESSION, i18n.inGame.inviteOnlySession,
                SessionType.CREW_SESSION, i18n.inGame.crewSession,
                SessionType.INVITE_ONLY_CREW_SESSION, i18n.inGame.inviteOnlyCrewSession,
                SessionType.INVITE_ONLY_FRIENDS_SESSION, i18n.inGame.inviteOnlyFriendsSession
        );

        private final VOptionButton<SessionType> joinANewSessionTypeOption = new VOptionButton<>(List.of(
                new VOptionButton.OptionItem<>(sessionTypeI18nMap.get(SessionType.PUBLIC_SESSION), SessionType.PUBLIC_SESSION),
                new VOptionButton.OptionItem<>(sessionTypeI18nMap.get(SessionType.INVITE_ONLY_SESSION), SessionType.INVITE_ONLY_SESSION),
                new VOptionButton.OptionItem<>(sessionTypeI18nMap.get(SessionType.CREW_SESSION), SessionType.CREW_SESSION),
                new VOptionButton.OptionItem<>(sessionTypeI18nMap.get(SessionType.INVITE_ONLY_CREW_SESSION), SessionType.INVITE_ONLY_CREW_SESSION),
                new VOptionButton.OptionItem<>(sessionTypeI18nMap.get(SessionType.INVITE_ONLY_FRIENDS_SESSION), SessionType.INVITE_ONLY_FRIENDS_SESSION)
        ));

        /* Join A Bookmarked Job */
        private final ToggleSwitch enableJoinABookmarkedJobToggle = new ToggleSwitch();
        private final VKeyChooseButton joinABookmarkedJobActivateKey = new VKeyChooseButton(ForkedKeyChooser.FLAG_WITH_KEY);
        private final ForkedSlider timeUtilJobsLoadedSlider = new ForkedSlider() {{
            setLength(300);
            setRange(500, 4000);
        }};

        public BPMSettingStage(ExtendedI18n i18n) {
            super(i18n);
            getContent().getChildren().addAll(contentBuilder()
                    .divide(bpmI18n.baseSetting.title)
                    .slider(bpmI18n.baseSetting.mouseScrollInterval, mouseScrollIntervalSlider)
                    .slider(bpmI18n.baseSetting.enterKeyInterval, enterKeyIntervalSlider)
                    .slider(bpmI18n.baseSetting.timeUtilPMenuLoaded, timeUtilPMenuLoadedSlider)
                    .divide(bpmI18n.joinANewSession.title)
                    .toggle(bpmI18n.joinANewSession.enable, enableJoinANewSessionToggle)
                    .button(bpmI18n.joinANewSession.activateKey, joinANewSessionActivateKey)
                    .button(bpmI18n.joinANewSession.sessionType, joinANewSessionTypeOption)
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
            joinANewSessionTypeOption.optionProperty().set(new VOptionButton.OptionItem<>(
                    sessionTypeI18nMap.get(bpmConfig.joinANewSession.sessionType), bpmConfig.joinANewSession.sessionType
            ));

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
            bpmConfig.joinANewSession.sessionType = joinANewSessionTypeOption.optionProperty().get().getOption();

            bpmConfig.joinABookmarkedJob.enable = enableJoinABookmarkedJobToggle.selectedProperty().get();
            bpmConfig.joinABookmarkedJob.activateKey = joinABookmarkedJobActivateKey.keyProperty().get();
            bpmConfig.joinABookmarkedJob.timeUtilJobsLoaded = timeUtilJobsLoadedSlider.valueProperty().get();
        }
    }
}
