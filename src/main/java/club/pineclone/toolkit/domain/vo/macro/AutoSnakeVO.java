package club.pineclone.toolkit.domain.vo.macro;

import club.pineclone.toolkit.core.macro.input.Key;
import lombok.Getter;

@Getter
public class AutoSnakeVO extends BetterMMenuVO {

    /**
     * 触发键位
     */
    private final Key activateKey;
    /**
     * 使用防弹衣
     */
    private final Boolean enableRefillVest;
    /**
     * 是否在退出时保留M菜单
     */
    private final Boolean enableKeepMMenu;

    public AutoSnakeVO(PercentageVO mouseScrollInterval,
                       PercentageVO keyPressInterval,
                       PercentageVO timeUtilMMenuLoaded,
                       Key menuKey,
                       Key activateKey,
                       Boolean enableRefillVest,
                       Boolean enableKeepMMenu) {

        super(mouseScrollInterval, keyPressInterval, timeUtilMMenuLoaded, menuKey);
        this.activateKey = activateKey;
        this.enableRefillVest = enableRefillVest;
        this.enableKeepMMenu = enableKeepMMenu;

    }
}
