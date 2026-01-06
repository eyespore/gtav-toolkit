package club.pineclone.gtavops.macro;

import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerEvent;
import club.pineclone.gtavops.macro.trigger.TriggerStatus;

/* 简单宏实现，构建trigger和action的连接并执行 */
public class SimpleMacro extends Macro {

    protected SimpleMacro(Trigger trigger, Action action) {
        super(trigger, action);
    }

    @Override
    public void onTriggerEvent(TriggerEvent event) {
        TriggerStatus status = event.getTriggerStatus();
        if (status.isAssert()) {
            /* 激活 */
            try {
                action.doActivate(MacroEvent.of(event, this));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else if (status.isRevoke()) {
            /* 撤销 */
            try {
                action.doDeactivate(MacroEvent.of(event, this));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
