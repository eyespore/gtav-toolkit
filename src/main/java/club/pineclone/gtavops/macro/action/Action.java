package club.pineclone.gtavops.macro.action;

import club.pineclone.gtavops.macro.MacroLifecycleAware;
import club.pineclone.gtavops.macro.trigger.TriggerEvent;
import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import lombok.Getter;
import lombok.Setter;


public abstract class Action implements ActionLifecycle, MacroLifecycleAware {

    /* 主要用于装饰器辨别当前父动作，以获取缓存的机器人实例 */
    @Getter protected final String actionId;
    /* 挂起状态位，当宏挂起时动作应该被拒绝执行（例如不在指定程序当中） */
    @Setter private boolean suspended = false;
    /* 激活状态位，如果需要子ACTION，那么可以通过这个状态位来控制ACTION是否被响应 */
    @Setter private boolean enable = true;

    public Action(final String actionId) {
        this.actionId = actionId;
    }

    public final void doActivate(ActionEvent event) throws Exception {
        if (suspended) return;  /* 若当前动作被挂起，那么拒绝执行 */
        if (!enable) return;  /* 若当前动作被禁用，那么拒绝执行 */

        boolean flag = beforeActivate(event);
        if (flag) {
            activate(event);
            afterActivate(event);
        }
    }

    public final void doDeactivate(ActionEvent event) throws Exception {
        if (suspended) return;  /* 若当前动作被挂起，那么拒绝停止 */
        if (!enable) return;  /* 若当前动作被禁用，那么拒绝停止 */

        boolean flag = beforeDeactivate(event);
        if (flag) {
            deactivate(event);
            afterDeactivate(event);
        }
    }

    @Override
    public final void onMarcoSuspend() {
        try {
            this.doDeactivate(new ActionEvent(TriggerEvent.ofNormal(null, null)));
            suspended = true;
        } catch (Exception e) {
            Logger.error(LogType.SYS_ERROR, e.getMessage());
        }
    }

    @Override
    public final void onMarcoResume() {
        suspended = false;
    }

    /**
     * 启用当前Action，注：Action在被创建之后默认处于启用状态，通常情况下你不需要二次启用某个Action，该方法
     * 更多被用于子Action的场景，例如某个父Action需要根据自己的生命周期控制另一个Action的情况
     */
    public final void enable() {
        enable = true;
    }

    /**
     * 禁用当前Action，该方法和enable类似，应该被用于子Action控制的场景
     */
    public final void disable() {
        enable = false;
    }
}
