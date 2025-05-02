package club.pineclone.gtavops.gui.theme;

import io.vproxy.vfx.manager.font.FontProvider;
import io.vproxy.vfx.manager.font.FontSettings;
import io.vproxy.vfx.manager.font.FontUsage;
import io.vproxy.vfx.theme.impl.DarkTheme;
import io.vproxy.vfx.theme.impl.DarkThemeFontProvider;
import javafx.scene.text.Font;

public class PinecloneTheme extends DarkTheme {

    private static final String FONT_NAME_FZMiaoWUS_GB = "FZMiaoWuS-GB";

    @Override
    public FontProvider fontProvider() {
        Font.loadFont(getClass().getResourceAsStream("/font/meow.ttf"), 1);
        return new PinecloneThemeFontProvider();
    }

    public static class PinecloneThemeFontProvider extends DarkThemeFontProvider {
        @Override
        protected void defaultFont(FontSettings settings) {
            settings.setFamily(FONT_NAME_FZMiaoWUS_GB);
        }

        @Override
        protected void windowTitle(FontSettings settings) {
            this.defaultFont(settings);
            settings.setSize(24.0);
        }

        @Override
        protected void _default(FontUsage usage, FontSettings settings) {
            this.defaultFont(settings);
            settings.setSize(25.0);
        }
    }
}
