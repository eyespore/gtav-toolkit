package club.pineclone.toolkit.domain.dto.macro;

import club.pineclone.toolkit.domain.entity.MacroEntry;
import lombok.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MacroConfigDTO {

    /**
     * 配置 ID
     */
    private UUID id;
    /**
     * 配置名称，用于向客户端显示
     */
    private String name = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(Instant.now().atZone(ZoneId.systemDefault()));
    /**
     * 配置版本号，对应宏内核版本
     */
    private String version;
    /**
     * 配置创建时间
     */
    private Instant createdAt;
    /**
     * 上一次修改时间
     */
    private Instant lastModifiedAt;
    /**
     * 宏列表
     */
    private List<MacroEntry> macros = new ArrayList<>();

}

