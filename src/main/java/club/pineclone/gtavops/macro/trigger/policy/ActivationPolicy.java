package club.pineclone.gtavops.macro.trigger.policy;

/* 触发模式，例如按住、切换 */

import club.pineclone.gtavops.macro.trigger.source.InputSource;

/**
 * @see HoldPolicy
 * @see TogglePolicy
 */
public interface ActivationPolicy {

    /**
     * 对{@link InputSource}传递的事件进行判定，给出是否可以执行下一步的判断
     * @param press 当前InputSource传递的按下、抬起事件
     * @return 1代表可以执行下一步、-1代表拒绝执行、0表示忽略
     */
    default int decide(boolean press) {
        return 0;
    }

    static ActivationPolicy Toggle() {
        return new TogglePolicy();
    }

    static ActivationPolicy Hold() {
        return new HoldPolicy();
    }

}
