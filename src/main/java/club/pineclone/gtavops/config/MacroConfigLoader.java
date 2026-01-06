package club.pineclone.gtavops.config;

import club.pineclone.gtavops.utils.JsonConfigUtils;
import club.pineclone.gtavops.utils.PathUtils;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MacroConfigLoader {

    private static ObjectMapper mapper;
    private static MacroConfig config;  /* 配置 */

    private static final Logger log = LoggerFactory.getLogger(MacroConfigLoader.class);

    private MacroConfigLoader() {}

    public static void load() throws IOException {
        SimpleModule module = new SimpleModule();
        ConfigCodecs.registerAll(module);
        mapper = new ObjectMapper();
        mapper.registerModule(module);  /* 注册模块 */
        mapper.enable(SerializationFeature.INDENT_OUTPUT);  /* 美观输出 */
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);  /* 即使读取到未知属性也不会报错 */

        config = JsonConfigUtils.load(PathUtils.getConfigFilePath(), MacroConfig::new, mapper);
        JsonConfigUtils.save(PathUtils.getConfigFilePath(), config, mapper);
        /* 若用户端配置存在无效属性，将会在这一步骤被一并移除 */
        /* 对于 Json 错误引起的异常则会被正常抛出 */
    }

    public static MacroConfig get() {
        return config;
    }

    /* 保存配置 */
    public static void save() throws IOException {
        log.debug("Save macro config to {}", PathUtils.getConfigFilePath());
        log.debug("DelayClimb status: {}", config.delayClimb.baseSetting.enable);
        JsonConfigUtils.save(PathUtils.getConfigFilePath(), config, mapper);
    }
}
