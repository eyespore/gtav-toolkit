package club.pineclone.toolkit.domain.dto.macro;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class BetterPMenuDTO {

    protected final PercentageDTO mouseScrollInterval;
    protected final PercentageDTO keyPressInterval;
    protected final PercentageDTO timeUtilPMenuLoaded;

}
