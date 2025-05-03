package club.pineclone.gtavops.i18n;

import io.vproxy.vfx.manager.internal_i18n.InternalI18n;
import lombok.Data;

@Data
public class ExtendedI18n implements InternalI18n {

    /* 通用 */
    public String toggle = "Toggle";
    public String hold = "Hold";
    public String unset = "<unset>";
    public String confirm = "Confirm";
    public String cancel = "Cancel";

//    public InGame inGame = new InGame();  /* 游戏内名词 */
    public SwapGlitch swapGlitch = new SwapGlitch();  /* 切枪偷速 */
    public QuickSnake quickSnake = new QuickSnake();  /* 回血增强 */
    public ADSwing adSwing = new ADSwing();  /* AD摇 */
    public MeleeGlitch meleeGlitch = new MeleeGlitch();  /* 近战武器偷速 */

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

    public String configFileLoadFailed = "config file load failed: {0}, do you want to OVERRIDE current config file?";
    public String configStillLoadFailed = "config file still load failed: {0}, you can try contact with PINECLONE, he's always glad to help :3";

    /* 主页 */
    @Data
    public static class Intro {
        public String title = "Introduction";
        public String header = "Coded By Pineclone";
        public String versionLabel = "version: ";
        public String acknowledgement = "UI design powered by wkgcass";
    }

    /* 功能选项 */
    @Data
    public static class Feature {
        public String title = "Enhance/Feature";
        public String header = "Right Click one of enhance/feature for further configuration!";
    }

    /* 字体包管理 */
    @Data
    public static class FontPack {
        public String header = "Font Pack Management";
        public String title = "Font Pack Manager";
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
    }

    /* 近战武器偷速 */
    @Data
    public static class MeleeGlitch {
        public String title = "Melee Glitch";
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
    public static class Config {
        public String title = "App Configuration";
        public String header = "Configure GTAV OPS!";
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
}
