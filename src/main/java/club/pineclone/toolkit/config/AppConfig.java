package club.pineclone.toolkit.config;

import lombok.Getter;

import java.nio.file.Path;

/**
 * 后端应用级别配置
 */
@Getter
public class AppConfig {

    private final Path coreHomePath;
    private final MacroSettings macroSettings;
    private final JsonMacroDataStoreSettings jsonMacroDataStoreSettings;

    public AppConfig(Path appHomePath) {
        this.coreHomePath = appHomePath.resolve("core");

        this.macroSettings = new MacroSettings(coreHomePath);
        this.jsonMacroDataStoreSettings = new JsonMacroDataStoreSettings(macroSettings.getHomePath());
    }

    @Getter
    public static class MacroSettings {
        /**
         * 宏功能家目录，JsonMacroConfigDao 依赖该家目录建立 configs.json 文件作为数据库，JsonMacroEntryDao 依赖该目录
         * 建立 entries.json 文件作为数据库
         */
        private final Path homePath;

        public MacroSettings(Path coreHomePath) {
            this.homePath = coreHomePath.resolve("macro");
        }
    }

    @Getter
    public static class JsonMacroDataStoreSettings {  /* 基于 Json 的宏功能存储 */
        private final Path macroConfigPath;
        private final Path macroEntryPath;

        public JsonMacroDataStoreSettings(Path macroHomePath) {
            this.macroConfigPath = macroHomePath.resolve("configs.json");  /* 基于 JSON 的 MacroConfig 数据表 */
            this.macroEntryPath = macroHomePath.resolve("entries.json");  /* 基于 JSON 的 MacroEntry 数据表 */
        }
    }
}
