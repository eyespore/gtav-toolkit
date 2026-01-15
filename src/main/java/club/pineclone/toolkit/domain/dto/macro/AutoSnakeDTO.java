package club.pineclone.toolkit.domain.dto.macro;

import club.pineclone.toolkit.core.macro.input.Key;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AutoSnakeDTO extends BetterMMenuDTO {

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

    public AutoSnakeDTO(PercentageDTO mouseScrollInterval,
                        PercentageDTO keyPressInterval,
                        PercentageDTO timeUtilMMenuLoaded,
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
