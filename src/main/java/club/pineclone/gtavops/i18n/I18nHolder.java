package club.pineclone.gtavops.i18n;

import club.pineclone.gtavops.utils.PathUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import io.vproxy.commons.util.Singleton;
import io.vproxy.vfx.manager.internal_i18n.InternalI18n;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class I18nHolder {

    private static final String LOCALE = "zhCN";

    public static ExtendedI18n get() {
        return Singleton.get(ExtendedI18n.class);
    }

    public static void load() throws IOException {
        ExtendedI18n i18n = loadI18n();
        Singleton.register(InternalI18n.class, i18n);
        Singleton.register(ExtendedI18n.class, i18n);  /* 加载本地化 */
    }

    private static ExtendedI18n loadI18n() {
        ExtendedI18n i18n;
        ExtendedI18n defaultI18n = new ExtendedI18n();  /* 默认本地化 */

        try (InputStream is = I18nHolder.class.getResourceAsStream("/i18n/" + LOCALE + ".json")) {
            if (is == null) {
                Logger.error(LogType.SYS_ERROR, "unable to load i18n file");
                i18n = defaultI18n;
            } else {
                /* 本地化文件存在 */
                ObjectMapper mapper = new ObjectMapper();
                mapper.enable(SerializationFeature.INDENT_OUTPUT);  /* 美观输出 */
                mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);  /* 忽略不存在的属性 */

                i18n = mapper.readerForUpdating(defaultI18n).readValue(is, ExtendedI18n.class);
                Logger.error(LogType.ACCESS, "successfully load i18n file");
            }
        } catch (IOException e) {
            i18n = defaultI18n;
        }

        return i18n;
    }

    /* 这个方法用于导出本地化pojo到json，从而创建更多的本地化配置 */
    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ExtendedI18n i18n = loadI18n();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        Path path = Path.of("zhCN.json");  /* 创建本地化文件 */
        mapper.writeValue(path.toFile(), i18n);
    }
}
