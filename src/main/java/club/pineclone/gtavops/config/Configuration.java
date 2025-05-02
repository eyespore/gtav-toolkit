package club.pineclone.gtavops.config;

import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.entity.input.KeyCode;
import io.vproxy.vfx.entity.input.MouseWheelScroll;
import javafx.scene.input.MouseButton;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class Configuration {
//    public InGame inGame = new InGame();
    public SwapGlitch swapGlitch = new SwapGlitch();
    public RestoreStrengthen restoreStrengthen = new RestoreStrengthen();

    /* 游戏热键 */
//    @Data
//    @ToString
//    public static class InGame {
//    }

    /* 切枪偷速 */
    @Data
    @ToString
    public static class SwapGlitch {
        public boolean enable = false;  /* 是否启用 */
        public double swapInterval = 50.0;  /* 切枪间隔 */
        public Key activatekey = new Key(MouseButton.BACK);  /* 激活热键 */
        public int activateMethod = 0;  /* 激活方式 0: 按住激活; 1: 切换激活 */
        public boolean swapMelee = false;  /* 进入偷速切换近战 */
        public boolean swapRanged = false;  /* 解除偷速切换远程 */

        public Key weaponWheel = new Key(new MouseWheelScroll(MouseWheelScroll.Direction.DOWN));  /* 武器轮盘 */
        public Key meleeWeapon = new Key(KeyCode.Q);  /* 近战武器 */
        public Key rangedWeapon = new Key(KeyCode.KEY_1);  /* 远程武器 */
    }

    /* 回血增强 */
    @Data
    @ToString
    public static class RestoreStrengthen {
        public boolean enable = false;
        public double triggerInterval = 20.0;  /* 点按间隔 */
        public Key activatekey = new Key(MouseButton.MIDDLE);  /* 激活热键 */
        public Key snakeKey = new Key(KeyCode.C);  /* 零食键 */
        public Key weaponWheel = new Key(KeyCode.TAB);  /* 武器轮盘 */
    }
}
