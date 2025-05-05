package club.pineclone.gtavops.macro.action;

public interface ScheduleLifecycle {

    /**
     * 循环具体逻辑
     */
    default void schedule(ActionEvent event) throws Exception {}

    /**
     * 循环开始前，可通过返回boolean来决定是否放行执行之后的schedule
     */
    default boolean beforeSchedule(ActionEvent event) throws Exception {
        return true;
    }

    /**
     * 循环结束后
     */
    default void afterSchedule(ActionEvent event) throws Exception {}

}
