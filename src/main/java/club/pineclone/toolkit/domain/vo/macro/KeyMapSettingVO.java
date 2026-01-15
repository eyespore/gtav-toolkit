package club.pineclone.toolkit.domain.vo.macro;

import club.pineclone.toolkit.core.macro.input.Key;

public record KeyMapSettingVO (
        Boolean enabled,
        Key sourceKey,
        Key targetKey
) {
}
