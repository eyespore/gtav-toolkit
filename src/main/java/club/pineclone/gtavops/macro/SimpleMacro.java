package club.pineclone.gtavops.macro;

import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.action.ActionEvent;
import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerEvent;

/* 简单宏实现，构建trigger和action的连接并执行 */
public class SimpleMacro extends Macro {

    public SimpleMacro(Trigger trigger, Action action) {
        super(trigger, action);
    }

    @Override
    public void onTriggerActivate(TriggerEvent event) {
        action.activate(ActionEvent.of(event));
    }

    @Override
    public void onTriggerDeactivate(TriggerEvent event) {
        action.deactivate(ActionEvent.of(event));
    }
}
