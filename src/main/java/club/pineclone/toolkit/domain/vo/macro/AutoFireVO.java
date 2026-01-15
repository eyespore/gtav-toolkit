package club.pineclone.toolkit.domain.vo.macro;

import club.pineclone.toolkit.common.TriggerMode;
import club.pineclone.toolkit.core.macro.input.Key;

public record AutoFireVO(
        Key activateKey,
        TriggerMode activateMethod,
        Key heavyWeaponKey,
        Key specialWeaponKey,
        PercentageVO triggerInterval,
        PercentageVO mousePressInterval
) {
}
