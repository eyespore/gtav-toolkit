package club.pineclone.gtavops.macro.action;

public interface ScheduleActionLifecycle extends ActionLifecycle {

    /**
     * 循环具体逻辑，该循环逻辑基于ScheduledExecutorService.scheduleAtFixedRate()方法执行
     */
    default void schedule(ActionEvent event) throws Exception {}

    /**
     * 每次循环之前，可通过返回boolean来决定是否放行执行之后的schedule
     */
    default boolean beforeSchedule(ActionEvent event) throws Exception {
        return true;
    }

    /**
     * 每次循环之后，执行后处理工作
     */
    default void afterSchedule(ActionEvent event) throws Exception {}

}
