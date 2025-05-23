package club.pineclone.test.utils;

import club.pineclone.gtavops.config.Config;
import club.pineclone.gtavops.utils.KeyUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.entity.input.KeyCode;
import junit.framework.TestCase;

import java.io.IOException;


public class ConfigTest extends TestCase {

    public void testToJsonMap() throws JsonProcessingException {

        TestConfig config = new TestConfig();

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String s = mapper.writeValueAsString(config);

        System.out.println(s);

        TestConfig testConfig = mapper.readValue(s, TestConfig.class);

        System.out.println(testConfig == config);
    }

    public void testConfiguration() throws JsonProcessingException {

        Config config = new Config();
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        module.addSerializer(Key.class, new KeySerializer());
        module.addDeserializer(Key.class, new KeyDeserializer());
        mapper.registerModule(module);

        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        String serialized = mapper.writeValueAsString(config);

        System.out.println(serialized);

        System.out.println(mapper.readValue(serialized, Config.class));
    }

    private static final class TestConfig {
        private boolean isEnable = false;

        @JsonSerialize(using = KeySerializer.class)
        @JsonDeserialize(using = KeyDeserializer.class)
        private Key hotkey = new Key(KeyCode.TAB);

        public boolean isEnable() {
            return isEnable;
        }

        public void setEnable(boolean enable) {
            isEnable = enable;
        }

        public Key getHotkey() {
            return hotkey;
        }

        public void setHotkey(Key hotkey) {
            this.hotkey = hotkey;
        }
    }

    /* Key反序列化 */
    private static class KeyDeserializer extends JsonDeserializer<Key> {
        @Override
        public Key deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            return KeyUtils.fromString(jsonParser.getText());
        }
    }

    private static class KeySerializer extends JsonSerializer<Key> {
        @Override
        public void serialize(Key key, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(KeyUtils.toString(key));
        }
    }

}
