package club.pineclone.toolkit.domain.dto.macro;


import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * 宏配置实例，每一个 MacroEntry 最终都会被映射到一个具体的宏实例对象
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public final class MacroEntryDTO {

    /**
     * 宏的唯一标识符，内核会维护当前至多一份配置文件生效，一份配置文件中可以定义多个宏实例，前端通过 UUID 来控制
     * 修改不同的宏实例对象
     */
    private UUID id;
    /**
     * 该宏实例所属的宏配置 ID
     */
    private UUID configId;
    /**
     * 宏实例的类型，内核维护了一份类型列表，Service层会对类型进行判断，以确定类型符合具体的要求
     */
    private String type;
    /**
     * 宏是否位于激活状态，若一个宏位于激活状态，当这份配置文件被加载时，宏就会被创建并生效
     */
    private Boolean enabled;
    /**
     * 宏实例创建时间
     */
    private Instant createdAt;
    /**
     * 宏实例最后修改时间
     */
    private Instant lastModifiedAt;
    /**
     * 该字段基于 MacroEntry 来实现 JsonNode 到 DTO 的转换
     */
    private Object config;
}
