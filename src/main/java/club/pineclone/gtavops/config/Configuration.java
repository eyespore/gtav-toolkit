package club.pineclone.gtavops.config;

import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.entity.input.KeyCode;
import io.vproxy.vfx.entity.input.MouseWheelScroll;
import javafx.scene.input.MouseButton;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Configuration {
//    public InGame inGame = new InGame();
    public SwapGlitch swapGlitch = new SwapGlitch();
    public QuickSnake quickSnake = new QuickSnake();
    public ADSwing adSwing = new ADSwing();
    public MeleeGlitch meleeGlitch = new MeleeGlitch();  /* 近战偷速 */
    public BetterMMenu betterMMenu = new BetterMMenu();  /* 更好的M菜单 */

    /* 游戏热键 */
//    @Data
//    @ToString
//    public static class InGame {
//    }

    public String gameHome = "";  /* 游戏路径 */

    /* 切枪偷速 */
    @Data
    public static class SwapGlitch {
        public boolean enable = false;  /* 是否启用 */
        public double triggerInterval = 50.0;  /* 切枪间隔 */
        public Key activatekey = new Key(MouseButton.BACK);  /* 激活热键 */
        public int activateMethod = 0;  /* 激活方式 0: 按住激活; 1: 切换激活 */
        public boolean enableSwapMelee = false;  /* 进入偷速切换近战 */
        public double postSwapMeleeDelay = 120.0;  /* 切换近战武器后等待时间 */
        public boolean enableSwapRanged = false;  /* 解除偷速切换远程 */

        public Key targetWeaponWheelKey = new Key(new MouseWheelScroll(MouseWheelScroll.Direction.DOWN));  /* 武器轮盘 */
        public Key meleeWeaponKey = new Key(KeyCode.Q);  /* 近战武器 */
        public Key rangedWeaponKey = new Key(KeyCode.KEY_1);  /* 远程武器 */

        public boolean enableSafetyWeaponWheel = false;
        public Key safetyWeaponWheelKey = new Key(KeyCode.TAB);
        public double safetyWeaponWheelDuration = 500.0;  /* 安全轮盘有效期 */
    }

    /* 回血增强 */
    @Data
    public static class QuickSnake {
        public boolean enable = false;
        public double triggerInterval = 20.0;  /* 点按间隔 */
        public Key activatekey = new Key(MouseButton.MIDDLE);  /* 激活热键 */
        public Key snakeKey = new Key(KeyCode.C);  /* 零食键 */
        public Key weaponWheel = new Key(KeyCode.TAB);  /* 武器轮盘 */
    }


    @Data
    public static class ADSwing {
        public boolean enable = false;
        public int activateMethod = 0;  /* 激活方式 0: 按住激活; 1: 切换激活 */
        public double triggerInterval = 20.0;  /* AD点按间隔 */
        public Key activatekey = new Key(MouseButton.MIDDLE);
        public Key moveLeftKey = new Key(KeyCode.A);
        public Key moveRightKey = new Key(KeyCode.D);
    }

    @Data
    public static class MeleeGlitch {
        public boolean enable = false;
        public int activateMethod = 0;  /* 激活方式 0: 按住激活; 1: 切换激活 */
        public double triggerInterval = 20.0;
        public Key meleeSnakeScrollKey = new Key(new MouseWheelScroll(MouseWheelScroll.Direction.UP));
        public Key activatekey = new Key(KeyCode.C);
    }

    @Data
    public static class BetterMMenu {
        public boolean enable = false;  /* 是否启用 */
        public Key menuKey = new Key(KeyCode.M);
        public double arrowKeyInterval = 20.0;
        public double enterKeyInterval = 20.0;
        public Key startEngineKey = new Key(MouseButton.FORWARD);
    }

}

