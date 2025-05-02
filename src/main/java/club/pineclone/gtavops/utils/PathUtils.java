package club.pineclone.gtavops.utils;

import com.github.kwhat.jnativehook.NativeSystem;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

public class PathUtils {
    public static final String CONFIG_DIR_NAME = ".gtav-ops";
    public static final String CONFIG_FILE_NAME = "config.json";

    public static String getArch() {
        return NativeSystem.getArchitecture().toString().toLowerCase();
    }

    public static String getOS() {
        return NativeSystem.getFamily().toString().toLowerCase();
    }

    public static String getJHome() {
        return System.getProperty("java.home");
    }

    public static String getUserHome() {
        return System.getProperty("user.home");
    }

    /* 配置文件路径( %appdata%/roaming/.gtav-ops/config.json ) */
    public static Path getConfigFilePath() {
        return appHomePath().resolve(CONFIG_FILE_NAME);
    }

    /* 本地化文件目录 */
    public static Path getI18NFilePath(String locale) throws IOException {
        URL resource = PathUtils.class.getResource("/i18n/" + locale + ".json");
        if (resource == null) {
            throw new IOException("i18n file not found");
        }
        return Path.of(resource.toExternalForm());
    }

    /* 配置目录路径( %appdata%/roaming/.gtav-ops/* (windows) ) */
    public static Path appHomePath() {
        String os = getOS();
        String userHome = getUserHome();
        Path configDir = null;
        if (os.contains("windows")) {
            configDir = Path.of(userHome, "AppData", "Roaming", CONFIG_DIR_NAME);
        } else if (os.contains("mac") || os.contains("darwin") || os.contains("nux") || os.contains("aix")) {
            configDir = Path.of(userHome, ".config", CONFIG_DIR_NAME);
        } else {
            configDir = Path.of(userHome, CONFIG_DIR_NAME);
        }
        return configDir;
    }
}
