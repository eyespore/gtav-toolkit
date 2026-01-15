package club.pineclone.toolkit.core.jni;

import club.pineclone.toolkit.AppLifecycleAware;
import club.pineclone.toolkit.common.PathUtils;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/* 平台焦点监听 */
public class PlatformFocusMonitor implements AppLifecycleAware {

    private final ScheduledExecutorService scheduler;
    private String lastTitle;
    private final Set<WindowTitleListener> listeners = new HashSet<>();
    private final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

    public PlatformFocusMonitor() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "platform-focus-scheduler");
            t.setDaemon(true);
            return t;
        });
    }

    public void onAppStart() {  /* 启动平台焦点监听 */
        System.load(loadDll().toAbsolutePath().toString());  /* 加载本地库 */

        log.info("Register platform focus monitor, ensure the macro can only be activate while target app is focusing");
        scheduler.scheduleAtFixedRate(this::poll, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void onAppStop() {
        scheduler.shutdownNow();
    }

    private Path loadDll() {
        String os = PathUtils.getOS();  // 操作系统
        String arch = PathUtils.getArch();  // 系统架构
        String jHome = PathUtils.getJHome();  // Java家目录
        String libName = System.mapLibraryName("PlatformFocusMonitor");

        Path dllPath = Path.of(jHome, "bin", "native", os, arch, libName);

        // ./bin/native/window/x86/PlatformFocusMonitor.dll
        if(Files.notExists(dllPath)){  // 在 ide 内环境时则采用默认位置
            dllPath = Path.of("assets", "native", os, arch, libName);
        }

        log.debug("Load native: {}", dllPath);
        return dllPath;
    }

    private void poll() {
        try {
            String title = getForegroundWindowTitle();
            if (title == null) return;
            if (!title.equals(lastTitle)) {
                lastTitle = title;
                listeners.forEach(listener -> listener.accept(title));
            }
        } catch (UnsatisfiedLinkError e) {
            log.error(e.getMessage(), e);
        } catch (Throwable t) {
            log.warn(t.getMessage(), t);
        }
    }

    public void addListener(WindowTitleListener listener) {
        listeners.add(listener);
    }

    public void removeListener(WindowTitleListener listener) {
        listeners.remove(listener);
    }

    private native String getForegroundWindowTitle();

    public static void main(String[] args) throws Exception {
        PlatformFocusMonitor monitor = new PlatformFocusMonitor();
        monitor.onAppInit();
        monitor.onAppStart();
        System.out.println(monitor.getForegroundWindowTitle());
    }
}
