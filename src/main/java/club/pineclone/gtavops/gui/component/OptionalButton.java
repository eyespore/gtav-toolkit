package club.pineclone.gtavops.gui.component;

import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.i18n.I18nHolder;
import io.vproxy.commons.util.Singleton;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.util.FXUtils;
import javafx.beans.property.*;
import javafx.geometry.Rectangle2D;

import java.util.ArrayList;
import java.util.List;

public class OptionalButton extends FusionButton {

    private final IntegerProperty indexProperty = new SimpleIntegerProperty(-1);
    private final ObjectProperty<OptionItem> itemProperty = new SimpleObjectProperty<>();
    private final List<OptionItem> items = new ArrayList<>();

    public OptionalButton() {
        StringProperty textProperty = new SimpleStringProperty();
        textProperty.addListener((observable, oldValue, newValue) -> {
            Rectangle2D textBounds = FXUtils.calculateTextBounds(getTextNode());
            setPrefWidth(Math.max(textBounds.getWidth() + 40, 120));
        });
        setPrefHeight(40);

        indexProperty.addListener((obs, oldVal, newVal) -> {
            int index = newVal.intValue();
            if (index >= 0 && index < items.size()) {
                itemProperty.set(items.get(index));
                textProperty.set(items.get(index).text);
            } else {
                itemProperty.set(null);
                textProperty.set(I18nHolder.get().unset);
            }
        });

        getTextNode().textProperty().bind(textProperty);
        setOnMouseClicked(event -> {
            if (items.size() == 1) return;
            if (indexProperty.get() >= items.size() -1) {
                indexProperty.set(0);
                return;
            }
            indexProperty.set(indexProperty.get() + 1);
        });
    }

    public void addOptionalItem(String text) {
        items.add(new OptionItem(text));
    }

    public ObjectProperty<OptionItem> itemProperty() {
        return itemProperty;
    }

    public IntegerProperty indexProperty() {
        return indexProperty;
    }

    public static class OptionItem {
        private final String text;

        public OptionItem(String text) {
            this.text = text;
        }
    }

}
