package club.pineclone.gtavops.macro;

import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerEvent;

public class TriggerBindings implements TriggerListener {

    protected Trigger trigger;  /* 触发器 */
    protected Action action;  /* 动作 */

    public TriggerBindings(Trigger trigger, Action action) {
        this.action = action;
        this.trigger = trigger;
        this.trigger.addListener(this);
    }

    @Override
    public void onTriggerActivate(TriggerEvent event) {
        action.activate();
    }

    @Override
    public void onTriggerDeactivate(TriggerEvent event) {
        action.deactivate();
    }

    public void install() {
        this.trigger.install();
    }

    public void uninstall() {
        this.trigger.removeListener(this);  /* 注销自身 */
        this.trigger.uninstall();  /* 注销执行器 */
    }
}
