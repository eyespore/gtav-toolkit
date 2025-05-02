package club.pineclone.gtavops.macro.action;

import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ScheduledAction extends Action implements ScheduleLifecycle {

    private final AtomicBoolean running = new AtomicBoolean(false);
    private ScheduledFuture<?> scheduledFuture;
    private final long interval;

    public ScheduledAction(long interval) {
        this.interval = interval;
    }

    @Override
    public final void activate() {
        if (running.compareAndSet(false, true)) {
            try {
                beforeSchedule();
                scheduledFuture = ActionTaskManager.getScheduler().scheduleAtFixedRate(() -> {
                            try {
                                schedule();
                            } catch (Exception e) {
                                Logger.error(LogType.SYS_ERROR, "exception occur during scheduling");
                                Thread.currentThread().interrupt();  // 处理异常
                            }
                        }, 0, interval, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public final void deactivate() {
        if (running.compareAndSet(true, false)) {
            if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
                scheduledFuture.cancel(true);
                try {
                    afterSchedule();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public long getInterval() {
        return interval;
    }
}
