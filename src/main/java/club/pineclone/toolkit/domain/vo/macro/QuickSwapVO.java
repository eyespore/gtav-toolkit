package club.pineclone.toolkit.domain.vo.macro;

import club.pineclone.toolkit.core.macro.input.Key;

import java.util.Map;

/**
 * 快速切枪宏，通过映射，当用户点击源键的时候，会自动触发目标键，并触发一次左键或Tab完成选择
 * @param keyMapping 按键映射
 * @param enableBlockKey 是否启用屏蔽键，当屏蔽键被按下时快速切枪不会被触发，通常用于和偷速互斥
 * @param blockKey 屏蔽键
 * @param blockDuration 屏蔽键生效时长
 */
public record QuickSwapVO(
        Map<Key, Key> keyMapping,
        Boolean enableBlockKey,
        Key blockKey,
        PercentageVO blockDuration
) {
}
