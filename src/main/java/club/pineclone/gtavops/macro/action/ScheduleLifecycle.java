package club.pineclone.gtavops.macro.action;

public interface ScheduleLifecycle {

    default void schedule() throws Exception {}  /* 循环逻辑 */

    default void beforeSchedule() throws Exception {}  /* 循环开始前 */

    default void afterSchedule() throws Exception {}  /* 循环结束后 */

}
