package club.pineclone.toolkit.domain.dto.macro;

import club.pineclone.toolkit.core.macro.input.Key;

public record KeyMappingDTO (
        Boolean enabled,
        Key sourceKey,
        Key targetKey
) {
}
