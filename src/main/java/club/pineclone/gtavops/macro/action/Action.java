package club.pineclone.gtavops.macro.action;

public abstract class Action {

    /* 执行动作 */
    public abstract void activate(ActionEvent event);

    /* 结束执行 */
    public abstract void deactivate(ActionEvent event);

}
