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
    public String unset = "<unset>";
    public String confirm = "Confirm";
    public String cancel = "Cancel";
    public String unknown = "Unknown";
    public String enabled = "Enabled";
    public String disabled = "Disabled";

    public String legacy = "Legacy";
    public String enhanced = "Enhanced";


    //    public InGame inGame = new InGame();  /* 游戏内名词 */
    public SwapGlitch swapGlitch = new SwapGlitch();  /* 切枪偷速 */
    public QuickSnake quickSnake = new QuickSnake();  /* 回血增强 */
    public ADSwing adSwing = new ADSwing();  /* AD摇 */
    public MeleeGlitch meleeGlitch = new MeleeGlitch();  /* 近战武器偷速 */
    public BetterMMenu betterMMenu = new BetterMMenu();  /* 更好的M菜单 */

    public FontPack fontPack = new FontPack();  /* 字体包管理 */
    public Feature feature = new Feature();  /* 功能特性 */
    public Intro intro = new Intro();  /* 主页 */
    public Config config = new Config();  /* 应用配置 */

    /* UI组件 */
    public String keyMouseChooserDesc = "press a key or mouse button for applying";
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
        public String fontpackExisted = "Detected existed fontpack resources at [{0}], would you like to import it into gtav-ops?";
        public String emptyGameHome = "/path/to/your/Grand Theft Auto V";
        public String confirmActivateFontpack = "Do you want to activate fontpack [{0}]?";
        public String confirmRemoveFontpack = "Are you sure you want to remove fontpack [{0}]?";

        public String emptyGameHomeAlert = "You have not choose 'Game Home' yet, choose your game home first.";
    }

    /* 切枪偷速 */
    @Data
    public static class SwapGlitch {
        public String title = "swap glitch";
        public String activateKey = "activate key";
        public String enableSwapMelee = "swap melee on activate";
        public String postSwapMeleeDelay = "delay after swapping melee";
        public String enableSwapRanged = "swap ranged weapon on deactivate";
        public String activateMethod = "activate method";
        public String triggerInterval = "trigger interval(ms)";
        public String preferredRangedKey = "preferred ranged weapon key";

        public String meleeKey = "melee weapon key";  /* 近战武器键 */
        public String targetWeaponWheelKey = "target weapon wheel key";  /* 目标武器轮盘 */

        public String enableSafetyWeaponWheel = "enable safety weapon wheel";  /* 启用安全武器轮盘 */
        public String safetyWeaponWheelKey = "safety weapon wheel key";  /* 安全武器轮盘键 */
        public String safetyWeaponWheelDuration = "safety weapon wheel duration";  /* 安全轮盘有效期 */
    }

    /* 近战武器偷速 */
    @Data
    public static class MeleeGlitch {
        public String title = "melee glitch";
        public String activateKey = "activate key";
        public String activateMethod = "activate method";  /* 激活方式 */
        public String meleeSnakeScrollKey = "melee snake scroll key";  /* 近战零食滚轮键 */
        public String triggerInterval = "trigger interval(ms)";
    }

    /* 快速零食 */
    @Data
    public static class QuickSnake {
        public String title = "quick snake";
        public String activateKey = "activate key";
        public String triggerInterval = "trigger interval(ms)";
        public String snakeKey = "snake key";
        public String weaponWheelKey = "weapon wheel key";
    }

    /* AD摇 */
    @Data
    public static class ADSwing {
        public String title = "AD swing";
        public String activateKey = "activate key";
        public String activateMethod = "activate method";
        public String triggerInterval = "trigger interval(ms)";
        public String moveLeftKey = "move left key";
        public String moveRightKey = "move right key";
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
        public String title = "better MMenu";
        public String menuKey = "menu key";  /* 互动菜单键 */
        public String arrowKeyInterval = "arrow key interval";  /* 方向键之后的等待时间 */
        public String enterKeyInterval = "enter interval";  /* 回车之后的等待时间 */
        public String startEngineKey = "start engine key";  /* 快速点火 */
        public String openVehicleDoor = "open vehicle";  /* 是否打开车门 */

    }

//    @Data
//    public static class InGame {
//    }

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
}
