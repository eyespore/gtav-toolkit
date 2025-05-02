package club.pineclone.gtavops.utils;

import com.github.kwhat.jnativehook.DefaultLibraryLocator;
import com.github.kwhat.jnativehook.NativeLibraryLocator;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JLibLocator implements NativeLibraryLocator {

    static {
        System.setProperty("jnativehook.lib.locator", JLibLocator.class.getCanonicalName());
    }

    private static final DefaultLibraryLocator defaultLocator = new DefaultLibraryLocator();

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
            return defaultLocator.getLibraries();
        }

        libs.add(libFile);
        return libs.iterator();
    }
}
