package club.pineclone.gtavops.macro.action;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ActionTaskManager {

    private static final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(
                    Runtime.getRuntime().availableProcessors(),
                    new NamedThreadFactory("app-scheduler-%d")
            );

    private ActionTaskManager() {}

    public static ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    public static void shutdown() {
        scheduler.shutdownNow();
    }

    private static class NamedThreadFactory implements ThreadFactory {
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
