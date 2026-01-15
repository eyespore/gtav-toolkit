package club.pineclone.toolkit.domain.vo.macro;

import club.pineclone.toolkit.common.TriggerMode;
import club.pineclone.toolkit.core.macro.input.Key;

import java.util.Map;

/**
 *
 * @param activateKey 激活热键
 * @param activateMethod 激活方式
 * @param weaponWheelKey 武器轮盘键
 * @param triggerInterval 触发间隔
 * @param enableSwapMeleeWeapon 启用结束偷速切换近战武器
 * @param meleeWeaponKey 近战武器键
 * @param postSwapMeleeWeaponDelay 切换到近战武器之后等待的延时，延时过低会导致无法触发近战武器偷速
 * @param enableSwapRangedWeapon 启用结束偷速切换到远程武器
 * @param defaultRangedWeaponKey 默认远程武器键位
 * @param swapDefaultRangedWeaponOnEmpty 在没有选择任何武器时自动选择默认远程武器
 * @param keyMapping 武器键位映射
 */
public record SwapGlitchVO(
        Key activateKey,
        TriggerMode activateMethod,
        Key weaponWheelKey,
        PercentageVO triggerInterval,
        Boolean enableSwapMeleeWeapon,
        Key meleeWeaponKey,
        PercentageVO postSwapMeleeWeaponDelay,
        Boolean enableSwapRangedWeapon,
        Key defaultRangedWeaponKey,
        Boolean swapDefaultRangedWeaponOnEmpty,
        Map<Key, Key> keyMapping
) {

}
