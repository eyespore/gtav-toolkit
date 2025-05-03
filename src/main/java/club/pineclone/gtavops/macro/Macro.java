package club.pineclone.gtavops.macro;

import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerListener;
import lombok.Getter;

/* 抽象宏 */
@Getter
public abstract class Macro implements TriggerListener {

    final Trigger trigger;
    final Action action;

    public Macro(final Trigger trigger, final Action action) {
        this.trigger = trigger;
        this.action = action;
        this.trigger.addListener(this);  /* 让触发器触发时提醒自己 */
    }

    public void install() {  /* 加载宏，例如注册监听器等 */
        this.trigger.install();
    }

    public void uninstall() {  /* 卸载宏，例如注销监听器等 */
        this.trigger.removeListener(this);  /* 注销自身 */
        this.trigger.uninstall();  /* 注销执行器 */
    }


}
