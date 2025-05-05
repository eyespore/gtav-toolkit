package club.pineclone.gtavops.macro.action;

public class ScheduledActionDecorator extends ScheduledAction {

    protected final ScheduledAction delegate;

    public ScheduledActionDecorator(final ScheduledAction delegate) {
        super(delegate.getInterval());
        this.delegate = delegate;
    }

    @Override
    public void schedule(ActionEvent event) throws Exception {
        delegate.schedule(event);
    }

    @Override
    public boolean beforeSchedule(ActionEvent event) throws Exception {
        return delegate.beforeSchedule(event);
    }

    @Override
    public void afterSchedule(ActionEvent event) throws Exception {
        delegate.afterSchedule(event);
    }
}
