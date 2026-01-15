package club.pineclone.toolkit.domain.dto.macro;

import club.pineclone.toolkit.core.macro.input.Key;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 更好的 M 菜单宏创建数据模型
 */
@Getter
@AllArgsConstructor
@ToString
public class BetterMMenuDTO {
    /**
     * 鼠标滚轮触发间隔
     */
    protected final PercentageDTO mouseScrollInterval;
    /**
     * 按键触发间隔
     */
    protected final PercentageDTO keyPressInterval;
    /**
     * 等待 M 菜单加载时间，解决 M 菜单启动后触发宏逻辑间隔过短导致的宏触发失败异常
     */
    protected final PercentageDTO timeUtilMMenuLoaded;
    /**
     * 游戏 M 菜单键位
     */
    protected final Key menuKey;
}
