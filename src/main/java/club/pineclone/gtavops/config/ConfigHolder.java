package club.pineclone.gtavops.config;

import club.pineclone.gtavops.utils.KeyUtils;
import club.pineclone.gtavops.utils.PathUtils;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.vproxy.vfx.entity.input.Key;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigHolder {

    private static ObjectMapper mapper;

    private static Configuration configuration;  /* 配置 */
    public static final String APPLICATION_TITLE = "GTAV Ops";  /* 应用基础信息 */
    public static final String APPLICATION_VERSION = "build00001-alpha6";

    private ConfigHolder() {}

    public static void load() throws IOException {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Key.class, new KeySerializer());  /* 按键序列化 */
        module.addDeserializer(Key.class, new KeyDeserializer());

        mapper = new ObjectMapper();
        mapper.registerModule(module);  /* 注册模块 */
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        Path configFilePath = PathUtils.getConfigFilePath();
        if (Files.notExists(configFilePath)) {
            /* 配置不存在，创建默认配置 */
            configuration = new Configuration();  /* 采用默认配置 */
            save();  /* 将配置写入文件 */
        } else {
            /* 配置存在，尝试加载配置 */
            configuration = mapper.readValue(configFilePath.toFile(), Configuration.class);
        }
    }

    public static void overrideConfigToDefault() throws IOException {
        configuration = new Configuration();
        save();  /* 将配置写入文件 */
    }

    public static Configuration get() {
        return configuration;
    }

    /* 保存配置 */
    public static void save() throws IOException {
        Path configFilePath = PathUtils.getConfigFilePath();
        mapper.writeValue(configFilePath.toFile(), configuration);  /* 保存配置 */
    }

    private static class KeySerializer extends JsonSerializer<Key> {
        @Override
        public void serialize(Key key, JsonGenerator generator, SerializerProvider provider) throws IOException {
            generator.writeString(KeyUtils.toString(key));
        }
    }

    private static class KeyDeserializer extends JsonDeserializer<Key> {
        @Override
        public Key deserialize(JsonParser parser, DeserializationContext context) throws IOException, JacksonException {
            return KeyUtils.fromString(parser.getText());
        }
    }
}
