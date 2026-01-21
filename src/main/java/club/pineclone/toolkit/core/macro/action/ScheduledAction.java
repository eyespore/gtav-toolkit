package club.pineclone.toolkit.core.macro.action;

import club.pineclone.toolkit.AppContext;
import club.pineclone.toolkit.core.macro.MacroContext;
import club.pineclone.toolkit.core.macro.MacroEvent;
import lombok.Getter;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ScheduledAction extends Action implements ScheduleActionLifecycle {

    private final AtomicBoolean running = new AtomicBoolean(false);
    private ScheduledFuture<?> scheduledFuture;
    @Getter private final long interval;

    private final long initialDelay;

    public ScheduledAction(String actionId, long interval) {
        this(actionId, interval, 0);
    }

    public ScheduledAction(String actionId, long interval, long initialDelay) {
        super(actionId);
        this.interval = interval;
        this.initialDelay = initialDelay;
    }

    @Override
    public final void activate(MacroEvent event) {
        if (running.compareAndSet(false, true)) {
            scheduledFuture = MacroContext.getInstance().getScheduler().scheduleAtFixedRate(() -> {
                boolean flag = beforeSchedule(event);
                if (flag) {
                    schedule(event);
                    afterSchedule(event);
                }
            }, initialDelay, interval, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public final void deactivate(MacroEvent event) {
        if (running.compareAndSet(true, false)) {
            if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
                scheduledFuture.cancel(true);
            }
        }
    }
}
