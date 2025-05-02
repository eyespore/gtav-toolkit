package club.pineclone.test.utils;

import club.pineclone.gtavops.i18n.ExtendedI18n;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import junit.framework.TestCase;

import java.io.IOException;
import java.nio.file.Path;

public class I18nTest extends TestCase {

    public void testI18nFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        ExtendedI18n i18n = new ExtendedI18n();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        Path path = Path.of("enUS.json");
        mapper.writeValue(path.toFile(), i18n);
    }
}
