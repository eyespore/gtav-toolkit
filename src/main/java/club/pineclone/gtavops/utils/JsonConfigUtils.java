package club.pineclone.gtavops.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

public class JsonConfigUtils {

    // TODO: 基于自定义实现类DeserializationProblemHandler实现收集多余配置项并返回报告
    /**
     * 基于路径读取配置文件，采用覆盖优先的策略，会基于提供的默认配置文件作为基底，使用通过文件读取到的配置覆盖默认配置
     *
     * @throws IOException 若没有显示配置FAIL_ON_UNKNOWN_PROPERTIES，那么在覆盖默认配置的过程中，若文件配置出现
     * 默认配置不存在的配置项，就会直接报错退出，可以利用这个特性，捕获所有的异常，告知用户哪些配置项是不该存在的
     */
    public static <T> T load(Path path, Supplier<T> defaultSupplier, ObjectMapper mapper) throws IOException {
        T defaultConfig = defaultSupplier.get();  /* 默认配置 */
        if (Files.notExists(path)) {  /* 配置文件不存在，返回默认配置 */
            return defaultConfig;
        }
        return mapper.readerForUpdating(defaultConfig).readValue(path.toFile());  /* 配置文件存在，更新后返回 */
    }

    public static <T> void save(Path path, T config, ObjectMapper mapper) throws IOException {
        mapper.writeValue(path.toFile(), config);
    }

    public static <T> void save(Path path, Supplier<T> configSupplier, ObjectMapper mapper) throws IOException {
        T config = configSupplier.get();
        mapper.writeValue(path.toFile(), config);  /* 保存配置 */
    }
}
