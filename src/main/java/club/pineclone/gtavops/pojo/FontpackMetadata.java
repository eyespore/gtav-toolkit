package club.pineclone.gtavops.pojo;

import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.i18n.I18nHolder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/* 字体包数据 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FontpackMetadata {
    private String id;  /* 字体包唯一id，使用uuid构建 */
    private String name;  /* 字体包名称 */
    private String desc;  /* 字体包描述 */
    private Integer type;  /* 字体包类型 0: 传承版 1: 增强版 */
    private Long size;  /* 文件大小，单位: byte */
    private Long createAt;  /* 字体包创建时间 */
    private Boolean enabled; /* 是否被启用 */

    public String formatType() {
        ExtendedI18n i18n = I18nHolder.get();
        ExtendedI18n.FontPack fpI18n = i18n.fontPack;
        return switch (type) {
            case 0 -> i18n.legacy;
            case 1 -> i18n.enhanced;
            default -> i18n.unknown;
        };
    }

    public ZonedDateTime formatCreatedAt() {
        return ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(createAt), ZoneId.systemDefault()
        );
    }

    public String formatEnabled() {
        ExtendedI18n i18n = I18nHolder.get();
        return enabled ? i18n.enabled : i18n.disabled;
    }

    public String formatSize() {
        return formatBytes(size);
    }

    private String formatBytes(long bytes) {
        final String[] units = {"B", "KB", "MB", "GB", "TB"};
        double size = bytes;
        int unitIndex = 0;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", size, units[unitIndex]);
    }

}
