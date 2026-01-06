package club.pineclone.gtavops.macro.action;

import club.pineclone.gtavops.macro.MacroEvent;

public class ScheduledActionDecorator extends ScheduledAction {

    protected final ScheduledAction delegate;

    public ScheduledActionDecorator(final ScheduledAction delegate) {
        super(delegate.getActionId(), delegate.getInterval());
        this.delegate = delegate;
    }

    @Override
    public void schedule(MacroEvent event) {
        delegate.schedule(event);
    }

    @Override
    public boolean beforeSchedule(MacroEvent event) {
        return delegate.beforeSchedule(event);
    }

    @Override
    public void afterSchedule(MacroEvent event) {
        delegate.afterSchedule(event);
    }
}
