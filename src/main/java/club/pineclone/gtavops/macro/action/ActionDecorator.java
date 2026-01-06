package club.pineclone.gtavops.macro.action;

import club.pineclone.gtavops.macro.MacroEvent;

public class ActionDecorator extends Action {

    protected final Action delegate;

    public ActionDecorator(final Action delegate) {
        super(delegate.actionId);
        this.delegate = delegate;
    }

    @Override
    public void activate(MacroEvent event) {
        delegate.activate(event);
    }

    @Override
    public void deactivate(MacroEvent event) {
        delegate.deactivate(event);
    }
}
