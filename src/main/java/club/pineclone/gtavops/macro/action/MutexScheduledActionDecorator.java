package club.pineclone.gtavops.macro.action;

/* todo: 引入动作优先级 */
/* 优先级互斥定时动作装饰器 */
public class MutexScheduledActionDecorator extends ScheduledAction  {

    protected final ScheduledAction delegate;

    public MutexScheduledActionDecorator(final ScheduledAction delegate) {
        super(delegate.getInterval());
        this.delegate = delegate;
    }

    @Override
    public void schedule() throws Exception {
        delegate.schedule();
    }

    @Override
    public void beforeSchedule() throws Exception {
        delegate.beforeSchedule();
    }

    @Override
    public void afterSchedule() throws Exception {
        delegate.afterSchedule();
    }


}
