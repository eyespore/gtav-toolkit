package club.pineclone.gtavops.utils;

import java.nio.file.Files;
import java.nio.file.Path;

public class JNIUtils {

    public static native String getForegroundWindowTitle();

    static {
        System.load(loadDll().toAbsolutePath().toString());
    }

    private static Path loadDll() {
        String os = PathUtils.getOS();  // 操作系统
        String arch = PathUtils.getArch();  // 系统架构
        String jHome = PathUtils.getJHome();  // Java家目录
        String libName = System.mapLibraryName("PlatformFocusMonitor");

        // ./bin/native/window/x86/PlatformFocusMonitor.dll
        Path dllPath = Path.of(jHome, "bin", "native", os, arch, libName);

        if(Files.notExists(dllPath)){  // 在ide内环境时则采用默认位置
            return Path.of("assets", "native", os, arch, libName);
        }

        return dllPath;
    }

    public static void main(String[] args) {
        System.out.println(getForegroundWindowTitle());
    }

}
