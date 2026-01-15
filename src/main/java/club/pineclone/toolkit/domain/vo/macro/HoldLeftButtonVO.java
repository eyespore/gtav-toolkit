package club.pineclone.toolkit.domain.vo.macro;

import club.pineclone.toolkit.common.TriggerMode;
import club.pineclone.toolkit.core.macro.input.Key;

/**
 * 辅助按住鼠标左键宏数据模型
 * @param activateMethod 触发模式
 * @param activateKey 触发键位
 */
public record HoldLeftButtonVO(
        TriggerMode activateMethod,
        Key activateKey
) {
}
