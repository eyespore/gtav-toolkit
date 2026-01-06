package club.pineclone.gtavops.macro;

import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerListener;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* 抽象宏 */
public abstract class Macro implements TriggerListener {

    protected final Trigger trigger;
    protected final Action action;

    private MacroState state;

    @Getter private MacroExecutionStatus executionStatus;  /* 执行状态 */

    protected final Logger log = LoggerFactory.getLogger(getClass());

    public enum MacroStatus {
        CREATED,  /* 宏在被创建之后，会处于CREATED状态，通过调用launch()使其切入RUNNING状态，CREATED状态支持挂起 */
        RUNNING,   /* 调用terminate()会切入TERMINATED状态*/
        TERMINATED  /* 宏生命周期结束，使用需要重新创建宏 */
    }

    public enum MacroExecutionStatus {
        ACTIVE, SUSPENDED
    }

    public interface MacroState {
        default void launch(Macro context) {}  /* 启动 */
        default void terminate(Macro context) {}  /* 停止 */
        MacroStatus getStatus();
    }
    
    /* 初始创建状态，该状态支持被挂起 */
    private static class CreatedState implements MacroState {
        @Override
        public void launch(Macro context) {
            context.state = new RunningState();
            context.trigger.addListener(context);  /* 将宏加入Trigger的监听列表，桥接开始 */
            context.trigger.onMacroLaunch(MacroEvent.of(null, context));
            context.action.onMacroLaunch(MacroEvent.of(null, context));  /* 告知生命周期 */
        }

        @Override
        public MacroStatus getStatus() {
            return MacroStatus.CREATED;
        }
    }
    
    /* 正在执行 */
    private static class RunningState implements MacroState {
        @Override
        public void terminate(Macro context) {
            context.state = new TerminatedState();
            context.trigger.removeListener(context);  /* 将宏从 Trigger 监听器列表注销，桥接结束 */
            context.action.onMacroTerminate(MacroEvent.of(null, context));
            context.trigger.onMacroTerminate(MacroEvent.of(null, context));  /* 注销执行器 */
        }

        @Override
        public MacroStatus getStatus() {
            return MacroStatus.RUNNING;
        }
    }
    
    /* 被关闭状态 */
    private static class TerminatedState implements MacroState {
        @Override
        public MacroStatus getStatus() {
            return MacroStatus.TERMINATED;
        }
    }

    public Macro(final Trigger trigger, final Action action) {
        this.trigger = trigger;
        this.action = action;
        this.state = new CreatedState();
        this.executionStatus = MacroExecutionStatus.ACTIVE;
    }

    /* 可以通过重复调用launch 和 terminate来灵活控制宏的加载和卸载，而不是调用GlobalScreen.registerNativeHook以及unregisterNativeHook */

    public void launch() {  /* 启用宏，例如注册监听器等 */
        this.state.launch(this);
    }

    /**
     * 停止宏，例如注销监听器等，需要注意的是，一旦某个宏被终止，那么它将不可以再被恢复
     */
    public void terminate() {  /*  */
        this.state.terminate(this);
    }

    /**
     * 挂起宏，此时会中断Trigger的事件输入，实现挂起的效果
     */
    public void suspend() {
        if (getStatus().equals(MacroStatus.TERMINATED)) return;  /* 宏生命周期结束，不支持挂起 */
        if (executionStatus.equals(MacroExecutionStatus.SUSPENDED)) return;  /* 宏当前已经处于挂起状态 */
        this.executionStatus = MacroExecutionStatus.SUSPENDED;

        if (getStatus().equals(MacroStatus.RUNNING)) {
            this.trigger.removeListener(this);
        }

        this.action.onMacroSuspend(MacroEvent.of(null, this));
        this.trigger.onMacroSuspend(MacroEvent.of(null, this));
    }

    /**
     * 恢复宏，会恢复Trigger的事件输入，从而恢复Action的执行
     */
    public void resume() {
        if (getStatus().equals(MacroStatus.TERMINATED)) return;  /* 宏生命周期已经结束，不支持恢复 */
        if (executionStatus == MacroExecutionStatus.ACTIVE) return;  /* 宏当前已经处于唤醒状态 */

        this.executionStatus = MacroExecutionStatus.ACTIVE;

        if (getStatus().equals(MacroStatus.RUNNING)) {  /* 仅在 RUNNING 状态下的挂起/恢复执行 */
            this.trigger.addListener(this);
        }

        this.action.onMacroResume(MacroEvent.of(null, this));
        this.trigger.onMacroResume(MacroEvent.of(null, this));
    }

    public MacroStatus getStatus() {
        return state.getStatus();
    }
}
