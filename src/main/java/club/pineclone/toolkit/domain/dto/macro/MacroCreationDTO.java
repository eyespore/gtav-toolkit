package club.pineclone.toolkit.domain.dto.macro;

/**
 * 宏创建输入对象，基于name字段指定创建的宏类型
 * @param <T>
 */
public record MacroCreationDTO(String type, Object config) {
}
