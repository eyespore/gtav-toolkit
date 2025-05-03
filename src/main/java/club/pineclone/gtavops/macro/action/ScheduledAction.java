package club.pineclone.gtavops.macro.action;

import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import lombok.Getter;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ScheduledAction extends Action implements ScheduleLifecycle {

    private final AtomicBoolean running = new AtomicBoolean(false);
    private ScheduledFuture<?> scheduledFuture;
    @Getter private final long interval;

    public ScheduledAction(long interval) {
        this.interval = interval;
    }

    @Override
    public final void activate(ActionEvent event) {
        if (event.isBlocked()) {
            /* 阻塞触发，不启动定时任务 */
            Logger.info(LogType.ALERT, "activation blocked");
            return;
        }

        if (running.compareAndSet(false, true)) {
            try {
                beforeSchedule();
                scheduledFuture = ActionTaskManager.getScheduler().scheduleAtFixedRate(() -> {
                            try {
                                schedule();
                            } catch (Exception e) {
                                Logger.error(LogType.ALERT, e.getMessage());
                                Thread.currentThread().interrupt();  // 处理异常
                            }
                        }, 0, interval, TimeUnit.MILLISECONDS);
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
                try {
                    afterSchedule();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
