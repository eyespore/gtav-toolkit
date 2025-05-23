package club.pineclone.gtavops.jni;

import club.pineclone.gtavops.utils.JNIUtils;
import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/* 平台焦点监听 */
public class PlatformFocusMonitor {

    private PlatformFocusMonitor() {}

    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "platform-focus-scheduler");
        t.setDaemon(true);
        return t;
    });

    static {
        SCHEDULER.scheduleAtFixedRate(PlatformFocusMonitor::poll, 0, 1000, TimeUnit.MILLISECONDS);
    }

    private static String lastTitle;
    private static final List<WindowTitleListener> listeners = new ArrayList<>();

    public static void shutdown() {
        SCHEDULER.shutdownNow();
    }

    private static void poll() {
        try {
            String title = JNIUtils.getForegroundWindowTitle();
            if (title == null) return;
            if (!title.equals(lastTitle)) {
                lastTitle = title;
                listeners.forEach(listener -> listener.accept(title));
            }
        } catch (Exception e) {
            Logger.warn(LogType.SYS_ERROR, e.getMessage());
        }
    }

    public static void addListener(WindowTitleListener listener) {
        listeners.add(listener);
    }
}
