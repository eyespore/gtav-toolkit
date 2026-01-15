package club.pineclone.toolkit.common;

import com.github.kwhat.jnativehook.DefaultLibraryLocator;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.NativeLibraryLocator;
import lombok.Getter;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;

public class JNativeHookManager implements NativeLibraryLocator {

    static {
        /* 手动提供 JNI */
//        log.info("Loading native library for jnativehook and terminate jnativehook logging ");  /* 加载 jnativehook 依赖的本地库 */
        System.setProperty("jnativehook.lib.locator", JNativeHookManager.class.getCanonicalName());

        /* 停止 jnativehook 日志记录 */
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
    }

    private static final Object LOCK = new Object();
    @Getter private static volatile boolean registered = false;
    private static final Set<Object> owners = new HashSet<>();

    public JNativeHookManager() {}  /* 保留 public 供 JNativeHook 反射创建实例 */

    public static void register(Object owner) throws NativeHookException {
        synchronized (LOCK) {
            if (owners.add(owner)) {
                if (!registered) {
                    GlobalScreen.registerNativeHook();
                    registered = true;
                }
            }
        }
    }

    public static void unregister(Object owner) throws NativeHookException {
        synchronized (LOCK) {
            if (!owners.remove(owner)) return; // 非法 / 重复 unregister，直接忽略
            if (owners.isEmpty() && registered) {
                GlobalScreen.unregisterNativeHook();
                GlobalScreen.setEventDispatcher(null);
                registered = false;
            }
        }
    }

    @Override
    public Iterator<File> getLibraries() {
        List<File> libs = new ArrayList<>(1);

        String os = PathUtils.getOS();  // 操作系统
        String arch = PathUtils.getArch();  // 系统架构
        String jHome = PathUtils.getJHome();  // Java家目录
        String libName = System.mapLibraryName("JNativeHook");

        // ./bin/native/window/x86/jnativehook.dll
        Path libPath = Path.of(jHome, "bin", "native", os, arch, libName);
        File libFile = libPath.toFile();

        if(!libFile.exists()){  // 在ide内环境时则采用默认位置
            return new DefaultLibraryLocator().getLibraries();
        }

        libs.add(libFile);
        return libs.iterator();
    }
}
