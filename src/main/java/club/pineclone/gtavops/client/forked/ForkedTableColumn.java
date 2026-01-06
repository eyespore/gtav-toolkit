package club.pineclone.gtavops.client.forked;

import club.pineclone.gtavops.client.theme.ExtendedFontUsages;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.table.VTableColumn;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

import java.util.function.Function;

public class ForkedTableColumn<S, T> extends VTableColumn<S, T> {
    public ForkedTableColumn(String name, Function<S, T> valueRetriever) {
        super(name, new Label(name) {{
            setAlignment(Pos.CENTER);
            FontManager.get().setFont(ExtendedFontUsages.tableHeadText, this);  /* 设置表头字号 */
            setTextFill(Theme.current().tableHeaderTextColor());
            setPrefHeight(25);
        }} , valueRetriever);
    }
}
