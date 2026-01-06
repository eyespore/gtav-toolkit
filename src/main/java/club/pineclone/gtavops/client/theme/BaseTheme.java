package club.pineclone.gtavops.client.theme;

import io.vproxy.vfx.manager.font.FontProvider;
import io.vproxy.vfx.manager.font.FontSettings;
import io.vproxy.vfx.manager.font.FontUsage;
import io.vproxy.vfx.manager.font.FontUsages;
import io.vproxy.vfx.theme.impl.DarkTheme;
import io.vproxy.vfx.theme.impl.DarkThemeFontProvider;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Consumer;

public class BaseTheme extends DarkTheme {

    @Getter
    @AllArgsConstructor
    public static class FontItem {
        private String title;
        private String path;
    }

    private static final FontItem FZMiaoWUS_GB = new FontItem("FZMiaoWuS-GB", "/font/FZMiaoWuS-GB.ttf");
    private static final FontItem MinecraftAEPixel = new FontItem("Minecraft AE Pixel", "/font/MinecraftAEPixel.ttf");
    private static final FontItem YangRenDongZhuShiTi_Light_2 = new FontItem("YRDZST-Light", "/font/YangRenDongZhuShiTi-Light-2.ttf");
    private static final FontItem QingSongShouXieTi_2 = new FontItem("清松手寫體2", "/font/QingSongShouXieTi2-2.ttf");

    private static final FontItem FONT_ON_USE = FZMiaoWUS_GB;

    public Color activeTextColor() {
        return Color.web("lightblue");
    }

    @Override
    public FontProvider fontProvider() {
        Font.loadFont(getClass().getResourceAsStream(FONT_ON_USE.getPath()), 1);
        return new BaseThemeFontProvider();
    }

    public static class BaseThemeFontProvider extends DarkThemeFontProvider {
        @Override
        protected void defaultFont(FontSettings settings) {
            settings.setFamily(FONT_ON_USE.getTitle());
//            settings.setFamily(FONT_NAME_MinecraftAEPixel);
//            super.defaultFont(settings);
        }

        @Override
        protected void windowTitle(FontSettings settings) {
            defaultFont(settings);
            settings.setSize(24.0);
        }

        /**
         * 默认字体大小
         */
        @Override
        protected void _default(FontUsage usage, FontSettings settings) {
            defaultFont(settings);
            settings.setSize(22.0);
        }

        @Deprecated
        @Override
        protected void tableCellText(FontSettings settings) {
            defaultFont(settings);
            settings.setSize(25.0);
        }

        /* 列表为空时的填充 */
        @Deprecated
        protected void tableEmptyTableLabel(FontSettings settings) {
            defaultFont(settings);
            settings.setSize(30.0);
        }

        /* 表头 */
        @Deprecated
        private void tableHeadText(FontSettings settings) {
            defaultFont(settings);
            settings.setSize(25.0);
        }

        @Deprecated
        private void textField(FontSettings settings) {
            defaultFont(settings);
            settings.setSize(25.0);
        }

        private void dividerText(FontSettings settings) {
            defaultFont(settings);
            settings.setSize(22.0);
        }

        @Override
        public void apply(FontUsage usage, FontSettings settings) {
            fontPack.getOrDefault(usage, s -> this._default(usage, s)).accept(settings);
        }

        private final Map<FontUsage, Consumer<FontSettings>> fontPack = Map.of(
                FontUsages.windowTitle, this::windowTitle,
                FontUsages.tableCellText, this::tableCellText,
                FontUsages.tableEmptyTableLabel, this::tableEmptyTableLabel,
                ExtendedFontUsages.tableHeadText, this::tableHeadText,
                ExtendedFontUsages.textField, this::textField,
                ExtendedFontUsages.dividerText, this::dividerText
        );
    }
}
