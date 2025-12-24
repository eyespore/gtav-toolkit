package club.pineclone.gtavops.macro.action;

import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import lombok.Getter;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ScheduledAction extends Action implements ScheduleActionLifecycle {

    private final AtomicBoolean running = new AtomicBoolean(false);
    private ScheduledFuture<?> scheduledFuture;
    @Getter private final long interval;

    private final long initialDelay;

    public ScheduledAction(String macroId, long interval) {
        this(macroId, interval, 0);
    }

    public ScheduledAction(String actionId, long interval, long initialDelay) {
        super(actionId);
        this.interval = interval;
        this.initialDelay = initialDelay;
    }

    @Override
    public final void activate(ActionEvent event) {
        if (running.compareAndSet(false, true)) {
            try {
                scheduledFuture = ActionTaskManager.getSCHEDULER().scheduleAtFixedRate(() -> {
                            try {

                                boolean flag = beforeSchedule(event);
                                if (flag) {
                                    schedule(event);
                                    afterSchedule(event);
                                }

                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();  // 处理异常
                            } catch (Exception e) {
                                Logger.error(LogType.ALERT, e.getMessage());
                            }
                        }, initialDelay, interval, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public final void deactivate(ActionEvent event) {
        if (running.compareAndSet(true, false)) {
            if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
                scheduledFuture.cancel(true);
            }
        }
    }
}
