package club.pineclone.gtavops.macro.action;

import lombok.Getter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ActionTaskManager {

    @Getter
    private static final ScheduledExecutorService SCHEDULER =
            Executors.newScheduledThreadPool(
                    Runtime.getRuntime().availableProcessors(),
                    new NamedThreadFactory("action-task-scheduler-%d")
            );

    private ActionTaskManager() {}

    public static void shutdown() {
        SCHEDULER.shutdownNow();
    }

    public static class NamedThreadFactory implements ThreadFactory {
        private final AtomicInteger count = new AtomicInteger();
        private final String pattern;
        public NamedThreadFactory(String pattern) { this.pattern = pattern; }
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, String.format(pattern, count.incrementAndGet()));
            t.setDaemon(true);
            return t;
        }
    }
}
