package club.pineclone.gtavops.client.component;

import club.pineclone.gtavops.client.theme.ExtendedFontUsages;
import club.pineclone.gtavops.utils.ColorUtils;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.theme.Theme;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

public class VTextField extends TextField {

    public VTextField() {
        setBackground(new Background(new BackgroundFill(
                Theme.current().sceneBackgroundColor(),
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));

        setBorder(new Border(new BorderStroke(
                Theme.current().windowBorderColorDark(),
                BorderStrokeStyle.SOLID,
                new CornerRadii(8),
                new BorderWidths(0.5))));

        FontManager.get().setFont(ExtendedFontUsages.textField, this);
        String textColor = ColorUtils.formatAsHex(Theme.current().normalTextColor());
        String promptColor = ColorUtils.formatAsHex(Theme.current().normalTextColor().darker());

        setStyle("-fx-text-fill: " + textColor + "; -fx-prompt-text-fill: " + promptColor + ";");
    }
}
