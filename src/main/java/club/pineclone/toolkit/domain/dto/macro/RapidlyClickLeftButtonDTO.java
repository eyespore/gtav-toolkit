package club.pineclone.toolkit.domain.dto.macro;

import club.pineclone.toolkit.common.TriggerMode;
import club.pineclone.toolkit.core.macro.input.Key;

/**
 * 辅助快速点击鼠标左键宏创建数据模型
 * @param activateMethod 触发模式
 * @param activateKey 触发按键
 * @param triggerInterval 触发间隔
 */
public record RapidlyClickLeftButtonDTO(
        TriggerMode activateMethod,
        Key activateKey,
        PercentageDTO triggerInterval
) {
}
