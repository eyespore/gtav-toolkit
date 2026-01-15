package club.pineclone.toolkit.domain.dto.macro;

import club.pineclone.toolkit.core.macro.input.Key;
import lombok.Getter;
import lombok.ToString;

/**
 * 快速点火宏创建数据模型
 */
@Getter
@ToString
public class StartEngineDTO extends BetterMMenuDTO {
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
    private final PercentageDTO doubleClickDetectInterval;

    public StartEngineDTO(PercentageDTO mouseScrollInterval,
                          PercentageDTO keyPressInterval,
                          PercentageDTO timeUtilMMenuLoaded,
                          Key menuKey,
                          Key activateKey,
                          Boolean enableDoubleClickToOpenDoor,
                          PercentageDTO doubleClickDetectInterval) {
        super(mouseScrollInterval, keyPressInterval, timeUtilMMenuLoaded, menuKey);
        this.activateKey = activateKey;
        this.enableDoubleClickToOpenDoor = enableDoubleClickToOpenDoor;
        this.doubleClickDetectInterval = doubleClickDetectInterval;
    }
}
