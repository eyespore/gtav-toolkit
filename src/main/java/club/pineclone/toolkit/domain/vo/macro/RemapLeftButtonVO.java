package club.pineclone.toolkit.domain.vo.macro;

import club.pineclone.toolkit.core.macro.input.Key;

/**
 * 鼠标左键重映射宏创建数据模型，通常用于将鼠标左键映射到某个键盘按键上
 * @param mapKey 键盘按键，该按键会被映射成鼠标
 */
public record RemapLeftButtonVO(
        Key mapKey
) {
}
