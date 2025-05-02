package club.pineclone.gtavops.i18n;

import club.pineclone.gtavops.utils.PathUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import io.vproxy.commons.util.Singleton;
import io.vproxy.vfx.manager.internal_i18n.InternalI18n;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class I18nHolder {

    private static ExtendedI18n i18n;

    public static ExtendedI18n get() {
        return Singleton.get(ExtendedI18n.class);
    }

    public static void load() throws IOException {
//        Path path = PathUtils.getI18NFilePath("enUS");  todo: 使用本地化文件
        Path path = Path.of("enUS.json");
        if (Files.notExists(path)) {
            Logger.error(LogType.SYS_ERROR, "unable to load i18n file: " + path);
            i18n = new ExtendedI18n();
        } else {
            /* 本地化文件存在 */
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            i18n = mapper.readValue(path.toFile(), ExtendedI18n.class);
        }
        Singleton.register(InternalI18n.class, i18n);
        Singleton.register(ExtendedI18n.class, i18n);  /* 加载本地化 */
    }

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ExtendedI18n i18n = new ExtendedI18n();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        Path path = Path.of("enUS.json");  /* 创建本地化文件 */
        mapper.writeValue(path.toFile(), i18n);
    }
}
