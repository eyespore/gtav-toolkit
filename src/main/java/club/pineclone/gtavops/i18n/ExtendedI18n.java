package club.pineclone.gtavops.i18n;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.vproxy.vfx.manager.internal_i18n.InternalI18n;
import lombok.Data;

import java.util.Random;
import java.util.Set;

@Data
public class ExtendedI18n implements InternalI18n {

    @JsonIgnore
    private static final Set<String> FAILURE_EMOJIS = Set.of(
          "😨", "😔", "🤔", "😫", "😥", "🥶"
    );

    /* 通用 */
    public String toggle = "Toggle";
    public String hold = "Hold";
    public String click = "Click";

    public String unset = "<unset>";
    public String confirm = "Confirm";
    public String cancel = "Cancel";
    public String unknown = "Unknown";
    public String enabled = "Enabled";
    public String disabled = "Disabled";

    public String legacy = "Legacy";
    public String enhanced = "Enhanced";

    public String keyboard = "keyboard";
    public String mouseButton = "Mouse button";
    public String mouseWheel = "Mouse wheel";


    public InGame inGame = new InGame();  /* 游戏内名词 */

    public SwapGlitch swapGlitch = new SwapGlitch();  /* 切枪偷速 */
    public QuickSnake quickSnake = new QuickSnake();  /* 回血增强 */
    public ADSwing adSwing = new ADSwing();  /* AD摇 */
    public MeleeGlitch meleeGlitch = new MeleeGlitch();  /* 近战武器偷速 */
    public BetterMMenu betterMMenu = new BetterMMenu();  /* 更好的M菜单 */
    public BetterLButton betterLButton = new BetterLButton();  /* 更好的鼠标左键 */
    public QuickSwap quickSwap = new QuickSwap();  /* 快速切枪 */
    public DelayClimb delayClimb = new DelayClimb();  /* 延迟攀 */
    public BetterPMenu betterPMenu = new BetterPMenu();  /* 额外功能 */

    public FontPack fontPack = new FontPack();  /* 字体包管理 */
    public Feature feature = new Feature();  /* 功能特性 */
    public Intro intro = new Intro();  /* 主页 */
    public Config config = new Config();  /* 应用配置 */

    /* UI组件 */
    public String keyChooserDescription = "press [{0}] to apply setting";
    public String keyChooserForwardMouseButton = "Forward MouseBtn";
    public String keyChooserBackMouseButton = "Back MouseBtn";
    public String keyChooserLeftMouseButton = "Left MouseBtn";
    public String keyChooserMiddleMouseButton = "Middle MouseBtn";
    public String keyChooserRightMouseButton = "Right MouseBtn";

    public String configFileLoadFailed = "config file load failed, be caution! Confirm will cause OVERRIDE CURRENT CONFIG FILE";
    public String configStillLoadFailed = "config file still load failed, you can try contact with PINECLONE, he's always glad to help :3";
    public String duplicatedAppInstanceRunning = "A duplicated app instance is still running! cannot launch another one";

    public String stacktraceAlertHeaderText = "Looks like we meet with some problem ";
    public String stacktraceAlertLabel = "If necessary please feel free copy the stack trace blow and send to developers!";
    public String stacktraceAlertTitle = "Hold On!";


    /* 游戏内名词 */
    @Data
    public static class InGame {
        public String publicSession = "public session";  /* 公开战局 */
        public String inviteOnlySession = "invite only session";  /* 邀请战局 */
        public String crewSession = "crew session";  /* 帮会战局 */
        public String inviteOnlyCrewSession = "invite only crew session";  /* 非公开帮会战局 */
        public String inviteOnlyFriendsSession = "invite only friends session";  /* 非公开好友战局 */
    }

    /* 主页 */
    @Data
    public static class Intro {
        public String title = "Introduction";
        public String header = "Coded By Pineclone";
        public String versionLabel = "version: ";
        public String acknowledgement = "UI design powered by wkgcass";

        public String introNavigate = "Intro";
        public String featureNavigate = "Feature";
        public String fontpackNavigate = "Fontpack";
    }

    /* 功能选项 */
    @Data
    public static class Feature {
        public String title = "Enhance/Feature";
        public String header = "Right Click one of feature for configuration!";
        public String gameVersion = "game version";
    }

    /* 字体包管理 */
    @Data
    public static class FontPack {
        public String title = "Font Pack Manager";
        public String gamePath = "Legacy Version";

        public String name = "name";
        public String desc = "description";
        public String type = "type";
        public String size = "size";
        public String createAt = "create At";
        public String status = "status";


        public String importFontpack = "Import";
        public String removeFontpack = "Remove";
        public String activateFontpack = "Activate";
        public String alreadyActivated = "Activated";
        public String chooseFontpackFile = "Choose Fontpack File";
        public String chooseGameHome = "Choose Game Home";
        public String fontpackFileDesc = "Fontpack file";

        public String fontpackName = "Fontpack name";
        public String defaultNaming = "My Fontpack";

        public String importingFontpack = "Importing Fontpack";
        public String importingFontpackDesc = "We could go faster, but then you wouldn't get to read this.";
        public String importSuccess = "import Fontpack Success";
        public String importFailure = "import Fontpack Failure";

        public String fontpackIsEnabled = "This fontpack is still enabled, you have to disable it first!";
        public String illegalGameHome = "Illegal game home was selected! Make sure you choose the right game home directory";
        public String fontpackExisted = "Detected existed fontpack resources with update.rpf: [{0}], update2.rpf: [{1}], would you like to import it into gtav-ops?";
        public String emptyGameHome = "/path/to/your/Grand Theft Auto V";
        public String confirmActivateFontpack = "Do you want to activate fontpack [{0}]?";
        public String confirmRemoveFontpack = "Are you sure you want to remove fontpack [{0}]?";

        public String copyingUpdate1File = "copying update.rpf...";
        public String copyingUpdate2File = "copying update2.rpf...";

        public String emptyGameHomeAlert = "You have not choose 'Game Home' yet, choose your game home first.";

        public String update1File = "update.rpf";
        public String update2File = "update2.rpf";

        public String chooseUpdate1File = "choose update.rpf";
        public String chooseUpdate2File = "choose update2.rpf";
        public String chooseFontpackResource = "choose fontpack resource";

        public String update1FileBtnText = "select update.rpf if exists";
        public String update2FileBtnText = "select update2.rpf is exists";
        public String illegalFontpackContribute = "illegal fontpack contribute, a fontpack should at least contain update.rpf or update2.rpf";
        public String illegalOriginalFontpackContribute = "illegal original fontpack contribute, you might need to execute 'Verify the integrity of game files' to fulfill your original fontpack";
    }

    /* 切枪偷速 */
    @Data
    public static class SwapGlitch {
        public String title = "swap glitch";
        public BaseSetting baseSetting = new BaseSetting();
        public SwapMeleeSetting swapMeleeSetting = new SwapMeleeSetting();
        public SwapRangedSetting swapRangedSetting = new SwapRangedSetting();

        /* 基础设置 */
        @Data
        public static class BaseSetting {
            public String title = "base settings";  /* 基础设置 */
            public String activateMethod = "activate method";
            public String targetWeaponWheelKey = "target weapon wheel key";  /* 目标武器轮盘 */
            public String activateKey = "activate key";
            public String triggerInterval = "trigger interval(ms)";
        }

        /* 切换近战武器设置 */
        @Data
        public static class SwapMeleeSetting {
            public String title = "swap melee settings";
            public String enable = "enable sub function";
            public String postSwapMeleeDelay = "delay after swapping melee";
            public String meleeKey = "melee weapon key";  /* 近战武器键 */
        }

        /* 切换远程武器设置 */
        @Data
        public static class SwapRangedSetting {
            public String title = "swap ranged settings";
            public String enable = "enable sub function";
            public String defaultRangedWeaponKey = "default ranged weapon key";
            public String listenRangedWeaponMapping = "listen ranged weapon mapping [{0}]";
            public String enableClearKey = "enable clear key";  /* 启用清除键 */
            public String clearKey = "clear key";  /* 清除键 */
        }
    }

    /* 近战武器偷速 */
    @Data
    public static class MeleeGlitch {
        public String title = "melee glitch";
        public BaseSetting baseSetting = new BaseSetting();

        public static class BaseSetting {
            public String title = "base settings";
            public String activateKey = "activate key";
            public String activateMethod = "activate method";  /* 激活方式 */
            public String meleeSnakeScrollKey = "melee snake scroll key";  /* 近战零食滚轮键 */
            public String triggerInterval = "trigger interval(ms)";
            public String safetyKey = "safety key";
            public String enableSafetyKey = "enable safety key";
        }
    }

    /* 快速零食 */
    @Data
    public static class QuickSnake {
        public String title = "quick snake";
        public BaseSetting baseSetting = new BaseSetting();

        @Data
        public static class BaseSetting {
            public String title = "base settings";
            public String activateKey = "activate key";
            public String triggerInterval = "trigger interval(ms)";
            public String snakeKey = "snake key";
            public String weaponWheelKey = "weapon wheel key";
        }
    }

    /* AD摇 */
    @Data
    public static class ADSwing {
        public String title = "AD swing";
        public BaseSetting baseSetting = new BaseSetting();

        @Data
        public static class BaseSetting {
            public String title = "base settings";
            public String activateKey = "activate key";
            public String activateMethod = "activate method";
            public String triggerInterval = "trigger interval(ms)";
            public String moveLeftKey = "move left key";
            public String moveRightKey = "move right key";
            public String safetyKey = "safety key";
            public String enableSafetyKey = "enable safety key";
        }
    }

    /* 应用配置 */
    @Data
    public static class Config {
        public String title = "App Configuration";
        public String header = "Configure GTAV OPS!";
    }

    /* 更好的M菜单 */
    @Data
    public static class BetterMMenu {
        public String title = "Better MMenu";
        public BaseSetting baseSetting = new BaseSetting();

        public static class BaseSetting {
            public String title = "base settings";
            public String menuKey = "menu key";  /* 互动菜单键 */
            public String mouseScrollInterval = "mouse scroll interval (ms)";  /* 方向键之后的等待时间 */
            public String enterKeyInterval = "enter interval (ms)";  /* 回车之后的等待时间 */
            public String startEngineKey = "start engine key";  /* 快速点火 */
            public String timeUtilMMenuLoaded = "time util menu loaded (ms)";  /* 等待M菜单加载时间 */
            public String openVehicleDoor = "open vehicle";  /* 是否打开车门 */
        }
    }

    @Data
    public static class BetterLButton {
        public String title = "Better LButton";
        public HoldLButtonSetting holdLButtonSetting = new HoldLButtonSetting();
        public RapidlyClickLButtonSetting rapidlyClickLButtonSetting = new RapidlyClickLButtonSetting();
        public RemapLButtonSetting remapLButtonSetting = new RemapLButtonSetting();

        public static class HoldLButtonSetting {
            public String title = "hold left button settings";
            public String enable = "enable auto hold left button";
            public String activateMethod = "activate method";
            public String activateKey =  "activate key";
        }

        public static class RapidlyClickLButtonSetting {
            public String title = "rapidly left button settings";
            public String enable = "enable auto rapidly click left button";
            public String activateMethod = "activate method";
            public String activateKey =  "activate key";
            public String triggerInterval = "trigger interval(ms)";
        }

        public static class RemapLButtonSetting {
            public String title = "remap left button settings";
            public String enable = "enable remap left button";
            public String activateKey = "activate key";
        }
    }

    /* 快速切枪 */
    public static class QuickSwap {
        public String title = "Quick Swap";
        public BaseSetting baseSetting = new BaseSetting();

        public static class BaseSetting {
            public String title = "base settings";
            public String quickSwapMapping = "enable mapping [{0}]";
            public String enableBlockKey = "enable block key";
            public String blockKey = "block key";
            public String blockDuration = "block duration(ms)";
        }
    }

    /* 延迟攀爬 */
    @Data
    public static class DelayClimb {
        public String title = "Delay Climb";
        public BaseSetting baseSetting = new BaseSetting();

        public static class BaseSetting {
            public String title = "base settings";
            public String toggleDelayClimbKey = "toggle delay climb key";
            public String usePhoneKey = "use phone key";  /* 使用手机 */
            public String hideInCoverKey = "hide in cover key";  /* 躲入掩体按键 */
            public String triggerInterval = "trigger interval(ms)";  /* 启用相机-关闭相机之间的间隔 */
            public String timeUtilCameraExited = "time util camera exited (ms)";  /* 相机退出等待时间1 */
            public String timeUtilCameraLoaded1 = "time util camera loaded 1 (ms)";  /* 相机退出等待时间2 */
            public String timeUtilCameraLoaded2 = "time util camera loaded 2 (ms)";  /* 相机退出等待时间2 */
        }
    }

    /* 额外功能 */
    @Data
    public static class BetterPMenu {
        public String title = "Better PMenu";
        public BaseSetting baseSetting = new BaseSetting();
        public JoinANewSession joinANewSession = new JoinANewSession();
        public JoinABookmarkedJob joinABookmarkedJob = new JoinABookmarkedJob();

        public static class BaseSetting {
            public String title = "base settings";
            public String mouseScrollInterval = "mouse scroll interval (ms)";  /* 方向键之后的等待时间 */
            public String enterKeyInterval = "enter interval (ms)";  /* 回车之后的等待时间 */
            public String timeUtilPMenuLoaded = "time util menu loaded (ms)";  /* 等待P菜单加载时间 */
        }

        public static class JoinANewSession {
            public String title = "join a new session";
            public String enable = "enable join a new session";
            public String activateKey = "activate key";
            public String sessionType = "session type";
        }

        /* 加入一个已收藏的差事 */
        public static class JoinABookmarkedJob {
            public String title = "join a bookmarked job";
            public String enable = "enable join a bookmarked session";
            public String activateKey = "activate key";
            public String timeUtilJobsLoaded = "time util jobs loaded (ms)";
        }
    }

    @Override
    public String keyChooserLeftMouseButton() {
        return keyChooserLeftMouseButton;
    }

    @Override
    public String keyChooserMiddleMouseButton() {
        return keyChooserMiddleMouseButton;
    }

    @Override
    public String keyChooserRightMouseButton() {
        return keyChooserRightMouseButton;
    }

    @Override
    public String stacktraceAlertHeaderText() {
        int skip = new Random().nextInt(FAILURE_EMOJIS.size());
        return stacktraceAlertHeaderText + FAILURE_EMOJIS.stream().skip(skip).findFirst().orElse(null);
    }

    @Override
    public String stacktraceAlertTitle() {
        return stacktraceAlertTitle;
    }

    @Override
    public String stacktraceAlertLabel() {
        return stacktraceAlertLabel;
    }

    public String toString() {
        return "";
    }
}
