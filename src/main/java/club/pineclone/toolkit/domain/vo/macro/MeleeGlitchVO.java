package club.pineclone.toolkit.domain.vo.macro;

import club.pineclone.toolkit.common.TriggerMode;
import club.pineclone.toolkit.core.macro.input.Key;

/**
 * 近战偷速宏，将滚轮键同时设置到零食和切换近战武器，就能够实现近战武器偷速，与常规偷速不同的是，
 * 近战偷速可以在偷速过程中吃零食恢复生命值，真正做到不掉速的血量回复
 * @param activateMethod 触发模式
 * @param activateKey 触发键位
 * @param enableSafetyKey 复合键位
 * @param safetyKey 复合键
 * @param triggerInterval 触发间隔
 * @param meleeSnakeScrollKey 近战武器&零食键
 */
public record MeleeGlitchVO(
        TriggerMode activateMethod,
        Key activateKey,
        Boolean enableSafetyKey,
        Key safetyKey,
        PercentageVO triggerInterval,
        Key meleeSnakeScrollKey
) {
}
