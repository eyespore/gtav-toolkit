package club.pineclone.test.domain;

import club.pineclone.toolkit.domain.entity.MacroConfig;
import club.pineclone.toolkit.domain.mapper.MacroConfigMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.UUID;

public class MacroConfigMapperTest {

    private final MacroConfigMapper macroConfigMapper = Mappers.getMapper(MacroConfigMapper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testCopy() throws JsonProcessingException {
        MacroConfig macroConfig = new MacroConfig();  /* 测试用 Entity */
        macroConfig.setId(UUID.randomUUID());
        macroConfig.setVersion("v0.1.0-alpha-5");
        macroConfig.setCreatedAt(Instant.now());
        macroConfig.setLastModifiedAt(Instant.now());

        System.out.println(macroConfig);
        MacroConfig copy = macroConfigMapper.copy(macroConfig);

        Assertions.assertNotSame(copy, macroConfig);  /* 经过拷贝后，两个对象应该不是同一个 */
    }

}
