package club.pineclone.toolkit.common;

import com.github.kwhat.jnativehook.NativeSystem;

import java.nio.file.Path;

public class PathUtils {

    private static final String APP_HOME_DIRECTORY_NAME = ".gtav-ops";

    public static String getArch() {
        return NativeSystem.getArchitecture().toString().toLowerCase();
    }

    public static String getOS() {
        return NativeSystem.getFamily().toString().toLowerCase();
    }

    public static String getJHome() {
        return System.getProperty("java.home");
    }

    /* 配置目录路径( %appdata%/roaming/.gtav-ops/* (windows) ) */
    public static Path getAppHomePath() {
        String os = getOS();
        String userHome = System.getProperty("user.home");
        Path configDir = null;
        if (os.contains("windows")) {
            configDir = Path.of(userHome, "AppData", "Roaming", APP_HOME_DIRECTORY_NAME);
        } else if (os.contains("mac") || os.contains("darwin") || os.contains("nux") || os.contains("aix")) {
            configDir = Path.of(userHome, ".configNode", APP_HOME_DIRECTORY_NAME);
        } else {
            configDir = Path.of(userHome, APP_HOME_DIRECTORY_NAME);
        }
        return configDir;
    }
}
