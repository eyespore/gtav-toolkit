package club.pineclone.toolkit.domain.vo.macro;

import club.pineclone.toolkit.core.macro.input.Key;
import lombok.Getter;

/**
 * 快速点火宏创建数据模型
 */
@Getter
public class StartEngineVO extends BetterMMenuVO {
    /**
     * 触发键位
     */
    private final Key activateKey;
    /**
     * 启用双击检测开门
     */
    private final Boolean enableDoubleClickToOpenDoor;
    /**
     * 双击检测间隔
     */
    private final PercentageVO doubleClickDetectInterval;

    public StartEngineVO(PercentageVO mouseScrollInterval,
                         PercentageVO keyPressInterval,
                         PercentageVO timeUtilMMenuLoaded,
                         Key menuKey,
                         Key activateKey,
                         Boolean enableDoubleClickToOpenDoor,
                         PercentageVO doubleClickDetectInterval) {
        super(mouseScrollInterval, keyPressInterval, timeUtilMMenuLoaded, menuKey);
        this.activateKey = activateKey;
        this.enableDoubleClickToOpenDoor = enableDoubleClickToOpenDoor;
        this.doubleClickDetectInterval = doubleClickDetectInterval;
    }
}
