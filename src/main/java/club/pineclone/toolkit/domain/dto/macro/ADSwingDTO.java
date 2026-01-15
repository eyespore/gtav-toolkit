package club.pineclone.toolkit.domain.dto.macro;

import club.pineclone.toolkit.common.TriggerMode;
import club.pineclone.toolkit.core.macro.input.Key;

/**
 * AD 摇宏创建数据模型
 * @param activateMethod 触发模式
 * @param activateKey 触发键位
 * @param enableSafetyKey 启用复合键
 * @param safetyKey 复合键位
 * @param triggerInterval 触发间隔
 * @param moveLeftKey 左移动键
 * @param moveRightKey 右移动键
 */
public record ADSwingDTO(
        TriggerMode activateMethod,
        Key activateKey,
        Boolean enableSafetyKey,
        Key safetyKey,
        PercentageDTO triggerInterval,
        Key moveLeftKey,
        Key moveRightKey
) {
}

