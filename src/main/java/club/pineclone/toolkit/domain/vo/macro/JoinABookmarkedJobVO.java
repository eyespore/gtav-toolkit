package club.pineclone.toolkit.domain.vo.macro;

import club.pineclone.toolkit.core.macro.input.Key;
import lombok.Getter;


@Getter
public class JoinABookmarkedJobVO extends BetterPMenuVO {

    private final Key activateKey;
    private final PercentageVO timeUtilJobListLoaded;

    public JoinABookmarkedJobVO(PercentageVO mouseScrollInterval,
                                PercentageVO keyPressInterval,
                                PercentageVO timeUtilPMenuLoaded,
                                Key activateKey,
                                PercentageVO timeUtilJobListLoaded) {
        super(mouseScrollInterval, keyPressInterval, timeUtilPMenuLoaded);
        this.activateKey = activateKey;
        this.timeUtilJobListLoaded = timeUtilJobListLoaded;
    }
}
