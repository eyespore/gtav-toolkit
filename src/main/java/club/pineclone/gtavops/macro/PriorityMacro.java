package club.pineclone.gtavops.macro;

import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerEvent;
import lombok.Getter;

/**
 * @see MacroManager 基于PriorityMacro完成宏挂起恢复、优先级控制
 */
@Getter
@Deprecated
public class PriorityMacro extends Macro {

    /* 宏当前状态 */
    public enum State {
        RUNNING, SUSPENDED, STOPPED
    }

    State state;
    final int priority;  /* 优先级 */

    public PriorityMacro(Trigger trigger, Action action, int priority) {
        super(trigger, action);
        this.priority = priority;
        this.state = State.STOPPED;
    }

    /* 仅允许宏处于正常运行状态下执行action的启停 */
    @Override
    public void onTriggerActivate(TriggerEvent event) {
        if (state != State.RUNNING) return;
//        action.activate();
    }

    @Override
    public void onTriggerDeactivate(TriggerEvent event) {
        if (state != State.RUNNING) return;
//        action.deactivate();
    }

    public void setState(State state) {
        if (this.state == state) return;
        switch (state) {
            case RUNNING -> resume();  /* 恢复宏 */
            case SUSPENDED -> suspend();  /* 挂起宏 */
            case STOPPED -> stop();  /* 停止宏 */
        }
    }

    protected void resume() {
        // 恢复
    }

    protected void suspend() {
        // 挂起
    }

    protected void stop() {
        // 停止
    }

    public String getMutexGroup() {
        return null;  /* 互斥组 */
    }
}
