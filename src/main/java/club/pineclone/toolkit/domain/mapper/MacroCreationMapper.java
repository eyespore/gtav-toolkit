package club.pineclone.toolkit.domain.mapper;

import org.mapstruct.Mapper;

/**
 * 面向宏创建数据模型的映射器，将 MacroEntry 中的 ConfigNode 在 VO 和 DTO 之间映射
 */
@Mapper(uses = {PercentageMapper.class})
public interface MacroCreationMapper {

}
