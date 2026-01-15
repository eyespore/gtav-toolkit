package club.pineclone.toolkit.domain.vo.macro;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BetterPMenuVO {

    protected final PercentageVO mouseScrollInterval;
    protected final PercentageVO keyPressInterval;
    protected final PercentageVO timeUtilPMenuLoaded;

}
