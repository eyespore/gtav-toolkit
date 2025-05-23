package club.pineclone.gtavops.macro.action;


/* 动作生命周期 */
public interface ActionLifecycle {

    /**
     * 执行动作之前，可通过返回值来控制是否放行当前动作
     */
    default boolean beforeActivate(ActionEvent event) throws Exception {
        return true;
    }

    default void activate(ActionEvent event) throws Exception {}

    /**
     * 动作执行之后，执行后处理工作
     */
    default void afterActivate(ActionEvent event) throws Exception {}

    /**
     * 动作撤销之前，可通过返回值来控制是否撤销当前动作
     */
    default boolean beforeDeactivate(ActionEvent event) throws Exception {
        return true;
    }

    /**
     * 动作撤销之后，执行撤销之后的后处理工作
     */
    default void afterDeactivate(ActionEvent event) throws Exception {}

    default void deactivate(ActionEvent event) throws Exception {}

}
