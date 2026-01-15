package club.pineclone.toolkit.domain.dto.macro;

import club.pineclone.toolkit.common.TriggerMode;
import club.pineclone.toolkit.core.macro.input.Key;

public record AutoFireDTO(
        Key activateKey,
        TriggerMode activateMethod,
        Key heavyWeaponKey,
        Key specialWeaponKey,
        PercentageDTO triggerInterval,
        PercentageDTO mousePressInterval
) {
}
