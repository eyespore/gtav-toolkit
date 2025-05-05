package club.pineclone.gtavops.gui.theme;

import io.vproxy.vfx.manager.font.FontProvider;
import io.vproxy.vfx.manager.font.FontSettings;
import io.vproxy.vfx.manager.font.FontUsage;
import io.vproxy.vfx.manager.font.FontUsages;
import io.vproxy.vfx.theme.impl.DarkTheme;
import io.vproxy.vfx.theme.impl.DarkThemeFontProvider;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Map;
import java.util.function.Consumer;

public class GTAVOpsBaseTheme extends DarkTheme {

    private static final String FONT_NAME_FZMiaoWUS_GB = "FZMiaoWuS-GB";

    public Color activeTextColor() {
        return Color.web("lightblue");
    }


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
            defaultFont(settings);
            settings.setSize(24.0);
        }

        @Override
        protected void _default(FontUsage usage, FontSettings settings) {
            defaultFont(settings);
            settings.setSize(25.0);
        }

        @Override
        protected void tableCellText(FontSettings settings) {
            defaultFont(settings);
            settings.setSize(25.0);
        }

        /* 列表为空时的填充 */
        protected void tableEmptyTableLabel(FontSettings settings) {
            defaultFont(settings);
            settings.setSize(30.0);
        }

        /* 表头 */
        private void tableHeadText(FontSettings settings) {
            defaultFont(settings);
            settings.setSize(25.0);
        }

        private void textField(FontSettings settings) {
            defaultFont(settings);
            settings.setSize(25.0);
        }

        @Override
        public void apply(FontUsage usage, FontSettings settings) {
            fontPack.getOrDefault(usage, s -> this._default(usage, s)).accept(settings);

            if (usage == FontUsages.windowTitle) {
                windowTitle(settings);
            } else if (usage == FontUsages.tableCellText) {
                tableCellText(settings);
            } else if (usage == FontUsages.tableEmptyTableLabel) {
                tableEmptyTableLabel(settings);
            } else {
                _default(usage, settings);
            }
        }

        private final Map<FontUsage, Consumer<FontSettings>> fontPack = Map.of(
                FontUsages.windowTitle, this::windowTitle,
                FontUsages.tableCellText, this::tableCellText,
                FontUsages.tableEmptyTableLabel, this::tableEmptyTableLabel,
                ExtendedFontUsages.tableHeadText, this::tableHeadText,
                ExtendedFontUsages.textField, this::textField
        );
    }
}
