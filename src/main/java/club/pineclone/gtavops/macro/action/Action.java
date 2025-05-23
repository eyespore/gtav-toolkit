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
    @Setter private boolean suspended = false;

    public Action(final String actionId) {
        this.actionId = actionId;
    }

    public void doActivate(ActionEvent event) throws Exception {
        if (suspended) return;
        boolean flag = beforeActivate(event);
        if (flag) {
            activate(event);
            afterActivate(event);
        }
    }

    public void doDeactivate(ActionEvent event) throws Exception {
        if (suspended) return;
        boolean flag = beforeDeactivate(event);
        if (flag) {
            deactivate(event);
            afterDeactivate(event);
        }
    }

    @Override
    public final void suspend() {
        try {
            this.doDeactivate(new ActionEvent(TriggerEvent.ofNormal(null, null)));
            suspended = true;
        } catch (Exception e) {
            Logger.error(LogType.SYS_ERROR, e.getMessage());
        }
    }

    @Override
    public final void resume() {
        suspended = false;
    }
}
