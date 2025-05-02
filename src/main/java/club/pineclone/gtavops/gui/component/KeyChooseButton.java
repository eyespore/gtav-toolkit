package club.pineclone.gtavops.gui.component;

import club.pineclone.gtavops.gui.forked.ForkedKeyChooser;
import club.pineclone.gtavops.i18n.I18nHolder;
import club.pineclone.gtavops.utils.KeyUtils;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.util.FXUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Rectangle2D;
import javafx.stage.Modality;

import java.util.Optional;

/**
 * 支持按键选择的按钮，按下按钮之后弹出弹窗等待用户按下按钮，可用于设定快捷键，仅支持单按键，
 * 支持滚轮、双侧键、鼠标按键以及键盘按键
 */
public class KeyChooseButton extends FusionButton {

    private boolean nullable = false;  /* 默认不支持空值 */
    private final ObjectProperty<Key> keyProperty = new SimpleObjectProperty<>();  /* 当前按键 */

    public KeyChooseButton() {
        this(ForkedKeyChooser.FLAG_WITH_KEY);
    }

    public KeyChooseButton(int flags) {
        StringProperty textProperty = new SimpleStringProperty();
        textProperty.addListener((observable, oldValue, newValue) -> {
            Rectangle2D textBounds = FXUtils.calculateTextBounds(getTextNode());
            setPrefWidth(Math.max(textBounds.getWidth() + 40, 120));
        });  /* 按钮大小自适应变化 */
        setPrefHeight(40);

        textProperty.bind(Bindings.createStringBinding(() -> {
            Key key = keyProperty.get();
            if (key == null) return I18nHolder.get().unset;
            return KeyUtils.toString(key);
        }, keyProperty));

        getTextNode().textProperty().bind(textProperty);

        setOnMouseClicked(event -> {
            // todo: ModdedKeyChooser作为静态final变量
            ForkedKeyChooser keyChooser = new ForkedKeyChooser(flags);
            keyChooser.getStage().getStage().initModality(Modality.APPLICATION_MODAL);
            Optional<Key> key = keyChooser.choose();

            key.ifPresentOrElse(keyProperty::set, () -> {
                if (nullable) keyProperty.set(null);
            });
        });
    }

    public ObjectProperty<Key> keyProperty() {
        return keyProperty;
    }

    // 是否支持空值
    public void setNullable(final boolean nullable) {
        this.nullable = nullable;
    }

}
