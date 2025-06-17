package club.pineclone.gtavops.config;

import club.pineclone.gtavops.macro.trigger.TriggerMode;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.entity.input.KeyCode;
import io.vproxy.vfx.entity.input.MouseWheelScroll;
import javafx.scene.input.MouseButton;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Config {
//    public InGame inGame = new InGame();
    public SwapGlitch swapGlitch = new SwapGlitch();
    public QuickSnake quickSnake = new QuickSnake();
    public ADSwing adSwing = new ADSwing();
    public MeleeGlitch meleeGlitch = new MeleeGlitch();  /* 近战偷速 */
    public BetterMMenu betterMMenu = new BetterMMenu();  /* 更好的M菜单 */
    public BetterLButton betterLButton = new BetterLButton();  /* 更好的鼠标左键 */
    public QuickSwap quickSwap = new QuickSwap();  /* 快速切枪 */

    public String gameHome = "";  /* 游戏路径 */

    /* 切枪偷速 */
    @Data
    public static class SwapGlitch {
        public BaseSetting baseSetting = new BaseSetting();
        public SwapMeleeSetting swapMeleeSetting = new SwapMeleeSetting();
        public SwapRangedSetting swapRangedSetting = new SwapRangedSetting();

        public static class BaseSetting {
            public boolean enable = false;  /* 是否启用 */
            public TriggerMode activateMethod = TriggerMode.HOLD;  /* 激活方式 0: 按住激活; 1: 切换激活 */
            public double triggerInterval = 50.0;  /* 切枪间隔 */
            public Key activatekey = new Key(MouseButton.BACK);  /* 激活热键 */
            public Key targetWeaponWheelKey = new Key(new MouseWheelScroll(MouseWheelScroll.Direction.DOWN));  /* 武器轮盘 */
        }

        public static class SwapMeleeSetting {
            public boolean enableSwapMelee = false;  /* 进入偷速切换近战 */
            public double postSwapMeleeDelay = 120.0;  /* 切换近战武器后等待时间 */
            public Key meleeWeaponKey = new Key(KeyCode.Q);  /* 近战武器 */
        }

        public static class SwapRangedSetting {
            public boolean enableSwapRanged = false;  /* 解除偷速切换远程 */
            public boolean swapDefaultRangedWeaponOnEmpty = false;  /* 切换默认远程武器如果没有指定武器 */
            public Key defaultRangedWeaponKey = new Key(KeyCode.KEY_1);  /* 远程武器 */

            public boolean enableMapping1 = false;  /* 启用映射1 */
            public Key mapping1SourceKey = new Key(KeyCode.KEY_1);  /* 映射1源键 */
            public Key mapping1TargetKey = new Key(KeyCode.KEY_6);  /* 映射1目标键 */

            public boolean enableMapping2 = false;  /* 启用映射2 */
            public Key mapping2SourceKey = new Key(KeyCode.KEY_2);
            public Key mapping2TargetKey = new Key(KeyCode.KEY_7);

            public boolean enableMapping3 = false;  /* 启用映射3 */
            public Key mapping3SourceKey = new Key(KeyCode.KEY_3);
            public Key mapping3TargetKey = new Key(KeyCode.KEY_8);

            public boolean enableMapping4 = false;  /* 启用映射4 */
            public Key mapping4SourceKey = new Key(KeyCode.KEY_4);
            public Key mapping4TargetKey = new Key(KeyCode.KEY_9);

            public boolean enableMapping5 = false;  /* 启用映射5 */
            public Key mapping5SourceKey = new Key(KeyCode.Q);
            public Key mapping5TargetKey = new Key(KeyCode.KEY_5);

            public boolean enableClearKey = false;  /* 启用屏蔽键 */
            public Key clearKey = new Key(KeyCode.TAB);  /* 屏蔽键 */
        }
    }

    /* 回血增强 */
    @Data
    public static class QuickSnake {
        public BaseSetting baseSetting = new BaseSetting();
        public static class BaseSetting {
            public boolean enable = false;
            public double triggerInterval = 40.0;  /* 点按间隔 */
            public Key activatekey = new Key(KeyCode.TAB);  /* 激活热键 */
            public Key snakeKey = new Key(KeyCode.MINUS);  /* 零食键 */
            public Key weaponWheel = new Key(KeyCode.TAB);  /* 武器轮盘 */
        }
    }


    @Data
    public static class ADSwing {
        public BaseSetting baseSetting = new BaseSetting();
        public static class BaseSetting {
            public boolean enable = false;
            public TriggerMode activateMethod = TriggerMode.HOLD;  /* 激活方式 0: 按住激活; 1: 切换激活 */
            public double triggerInterval = 20.0;  /* AD点按间隔 */
            public Key activatekey = new Key(KeyCode.E);
            public Key moveLeftKey = new Key(KeyCode.A);
            public Key moveRightKey = new Key(KeyCode.D);
            public Key safetyKey = new Key(MouseButton.BACK);
            public boolean enableSafetyKey = true;
        }
    }

    @Data
    public static class MeleeGlitch {
        public BaseSetting baseSetting = new BaseSetting();
        public static class BaseSetting {
            public boolean enable = false;
            public TriggerMode activateMethod = TriggerMode.HOLD;  /* 激活方式 0: 按住激活; 1: 切换激活 */
            public double triggerInterval = 20.0;
            public Key meleeSnakeScrollKey = new Key(new MouseWheelScroll(MouseWheelScroll.Direction.UP));
            public Key activatekey = new Key(KeyCode.E);
            public Key safetyKey = new Key(MouseButton.BACK);
            public boolean enableSafetyKey = true;
        }
    }

    @Data
    public static class BetterMMenu {
        public BaseSetting baseSetting = new BaseSetting();
        public static class BaseSetting {
            public boolean enable = false;  /* 是否启用 */
            public Key menuKey = new Key(KeyCode.M);
            public double arrowKeyInterval = 20.0;
            public double enterKeyInterval = 20.0;
            public Key startEngineKey = new Key(MouseButton.MIDDLE);
        }
    }

    @Data
    public static class BetterLButton {
        public RapidlyClickLButtonSetting rapidlyClickLButtonSetting = new RapidlyClickLButtonSetting();
        public HoldLButtonSetting holdLButtonSetting = new HoldLButtonSetting();
        public BaseSetting baseSetting = new BaseSetting();

        public static class BaseSetting {
            public boolean enable = false;
        }

        public static class HoldLButtonSetting {
            public boolean enable = false;
            public TriggerMode activateMethod = TriggerMode.TOGGLE;  /* 激活方式 0: 按住激活; 1: 切换激活 */
            public Key activateKey = new Key(KeyCode.C);
        }

        public static class RapidlyClickLButtonSetting {
            public boolean enable = false;
            public TriggerMode activateMethod = TriggerMode.TOGGLE;  /* 激活方式 0: 按住激活; 1: 切换激活 */
            public Key activateKey = new Key(KeyCode.V);
            public double triggerInterval = 20.0;
        }
    }

    /* 快速切枪 */
    public static class QuickSwap {
        public String title = "quick swap";
        public BaseSetting baseSetting = new BaseSetting();

        public static class BaseSetting {
            public boolean enable = false;
            public boolean enableMapping1 = false;
            public boolean enableMapping2 = false;
            public boolean enableMapping3 = false;
            public boolean enableMapping4 = false;
            public boolean enableMapping5 = false;

            public boolean enableBlockKey = true;
            public Key blockKey = new Key(MouseButton.BACK);
            public double blockDuration = 500.0;
        }
    }

}

