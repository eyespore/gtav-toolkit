package club.pineclone.gtavops.macro.action;

public class ScheduledActionDecorator extends ScheduledAction {

    protected final ScheduledAction delegate;

    public ScheduledActionDecorator(final ScheduledAction delegate) {
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
