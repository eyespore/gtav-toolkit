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
    public RestoreStrengthen restoreStrengthen = new RestoreStrengthen();  /* 回血增强 */

    /* 主页 */
    public String introSceneTitle = "Introduction";
    public String introSceneHeader = "Coded By Pineclone";
    public String versionLabel = "version: ";
    public String acknowledgement = "UI design powered by wkgcass";

    /* UI组件 */
    public String keyMouseChooserDesc = "press a key or mouse button for applying";
    public String keyChooserForwardMouseButton = "Forward MouseBtn";
    public String keyChooserBackMouseButton = "Back MouseBtn";
    public String keyChooserLeftMouseButton = "Left MouseBtn";
    public String keyChooserMiddleMouseButton = "Middle MouseBtn";
    public String keyChooserRightMouseButton = "Right MouseBtn";

    public String configFileLoadFailed = "config file load failed: {0}, do you want to OVERRIDE current config file?";
    public String configStillLoadFailed = "config file still load failed: {0}, you can try contact with PINECLONE, he's always glad to help :3";

    /* 功能选项 */
    public String featureSceneTitle = "Enhance/Feature";
    public String featureSceneHeader = "Right Click one of enhance/feature for further configuration!";

    /* 应用设置 */
    public String settingSceneTitle = "Application Settings";
    public String settingSceneHeader = "Application Configuration";

    /* 切枪偷速 */
    @Data
    public static class SwapGlitch {
        public String title = "swap glitch";
        public String activateKey = "activate key";
        public String swapMeleeOnActivate = "swap melee on activate";
        public String swapRangedOnDeactivate = "swap ranged weapon on deactivate";
        public String activateMethod = "activate method";
        public String swapInterval = "swap interval(ms)";
        public String preferredRangedKey = "preferred ranged weapon key";

        public String meleeKey = "melee weapon key";  /* 近战武器键 */
        public String weaponWheelKey = "weapon wheel key";  /* 轮盘键 */
    }

    @Data
    public static class RestoreStrengthen {
        public String title = "restore strengthen";
        public String activateKey = "activate key";
        public String triggerInterval = "trigger interval(ms)";
        public String snakeKey = "snake key";
        public String weaponWheelKey = "weapon wheel key";
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
