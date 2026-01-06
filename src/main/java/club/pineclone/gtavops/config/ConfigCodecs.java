package club.pineclone.gtavops.config;

import club.pineclone.gtavops.common.SessionType;
import club.pineclone.gtavops.utils.KeyUtils;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.vproxy.base.util.Logger;
import io.vproxy.vfx.entity.input.Key;

import java.io.IOException;

/**
 * 配置项序列化以及反序列化
 */
public class ConfigCodecs {

    /* 序列化反序列化注册 */
    public static void registerAll(SimpleModule module) {
        module.addSerializer(Key.class, new ConfigCodecs.KeySerializer());  /* 按键序列化 */
        module.addDeserializer(Key.class, new ConfigCodecs.KeyDeserializer());

        module.addSerializer(SessionType.class, new SessionTypeSerializer());  /* 战局类型序列化 */
        module.addDeserializer(SessionType.class, new SessionTypeDeserializer());
    }

    /* Key 对象序列化 */
    public static class KeySerializer extends JsonSerializer<Key> {
        @Override
        public void serialize(Key key, JsonGenerator generator, SerializerProvider provider) throws IOException {
            generator.writeString(KeyUtils.toString(key));
        }
    }

    /* Key 对象反序列化 */
    public static class KeyDeserializer extends JsonDeserializer<Key> {
        @Override
        public Key deserialize(JsonParser parser, DeserializationContext context) throws IOException, JacksonException {
            return KeyUtils.fromString(parser.getText());
        }
    }

    /* SessionType 对象序列化 */
    public static class SessionTypeSerializer extends JsonSerializer<SessionType> {
        @Override
        public void serialize(SessionType sessionType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(sessionType.name());
//            Logger.lowLevelDebug(sessionType.name());
        }
    }

    /* SessionType 反序列化 */
    public static class SessionTypeDeserializer extends JsonDeserializer<SessionType> {
        @Override
        public SessionType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
//            Logger.lowLevelDebug(jsonParser.getText());
            return SessionType.valueOf(jsonParser.getText());
        }
    }
}
