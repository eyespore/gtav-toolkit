package club.pineclone.gtavops.macro.action;

import club.pineclone.gtavops.macro.MacroLifecycleAware;
import lombok.Getter;

@Getter
public abstract class Action implements ActionLifecycle, MacroLifecycleAware {

    /* 主要用于装饰器辨别当前父动作，以获取缓存的机器人实例 */
    protected final String actionId;

    public Action(final String actionId) {
        this.actionId = actionId;
    }

    /* 执行动作 */
    public abstract void activate(ActionEvent event);

    /* 结束执行 */
    public abstract void deactivate(ActionEvent event);


    public void doActivate(ActionEvent event) throws Exception {
        boolean flag = beforeActivate(event);
        if (flag) {
            activate(event);
            afterActivate(event);
        }
    }

    public void doDeactivate(ActionEvent event) throws Exception {
        boolean flag = beforeDeactivate(event);
        if (flag) {
            deactivate(event);
            afterDeactivate(event);
        }
    }

}
