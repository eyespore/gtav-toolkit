package club.pineclone.toolkit.domain.mapper;

import club.pineclone.toolkit.domain.entity.MacroEntry;
import com.fasterxml.jackson.databind.JsonNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface MacroEntryMapper {

    /**
     * 为一份MacroEntry创建拷贝，面向Dao层
     * @param source 拷贝数据源
     * @return 拷贝结果
     */
    @Mapping(target = "configNode", qualifiedByName = "copyConfigNode")
    MacroEntry copy(MacroEntry source);

    @Named("copyConfigNode")
    default JsonNode copyConfigNode(JsonNode configNode) {
        return configNode.deepCopy();
    }

}
