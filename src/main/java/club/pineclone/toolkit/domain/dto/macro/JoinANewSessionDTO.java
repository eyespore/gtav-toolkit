package club.pineclone.toolkit.domain.dto.macro;

import club.pineclone.toolkit.common.SessionType;
import club.pineclone.toolkit.core.macro.input.Key;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class JoinANewSessionDTO extends BetterPMenuDTO {

    private final Key activateKey;
    private final SessionType sessionType;

    public JoinANewSessionDTO(PercentageDTO mouseScrollInterval,
                              PercentageDTO keyPressInterval,
                              PercentageDTO timeUtilPMenuLoaded,
                              Key activateKey,
                              SessionType sessionType) {
        super(mouseScrollInterval, keyPressInterval, timeUtilPMenuLoaded);
        this.activateKey = activateKey;
        this.sessionType = sessionType;
    }
}
