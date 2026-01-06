package club.pineclone.gtavops.client.component;

import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.util.FXUtils;
import javafx.beans.property.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseButton;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

/**
 * 循环按钮，通过传入一组OptionItem，单击鼠标左键或右键进行循环式选择，左键可以切换到下一个选项，右键
 * 则可以切换到上一个选项
 */
public class VOptionButton<T> extends FusionButton {

    private final ObjectProperty<OptionItem<T>> optionProperty = new SimpleObjectProperty<>();

    public VOptionButton(List<OptionItem<T>> options) {
        /* 使大小跟随内容动态变化，避免内容超出按钮本身大小 */
        getTextNode().textProperty().addListener((observable, oldValue, newValue) -> {
            Rectangle2D textBounds = FXUtils.calculateTextBounds(getTextNode());
            setPrefWidth(Math.max(textBounds.getWidth() + 40, 120));
        });
        setPrefHeight(40);

        /* 处理鼠标点击事件，鼠标点击触发索引变化 */
        setOnMouseClicked(event -> {
            if (options.size() == 1) return;  /* 若仅有一个选项，那么忽略点击事件 */
            MouseButton button = event.getButton();

            int currentIndex = options.indexOf(optionProperty.get());
            int targetIndex = 0;

            if (button.equals(MouseButton.PRIMARY)) {  /* 鼠标左键点击，切换到下一个CycleOption */
                targetIndex = currentIndex == options.size() - 1 ? 0 : currentIndex + 1;

            } else if (button.equals(MouseButton.SECONDARY)) {  /* 鼠标右键点击，切换到上一个CycleOption */
                targetIndex = currentIndex == 0 ? options.size() - 1 : currentIndex - 1;
            }

            optionProperty.set(options.get(targetIndex));
        });

        /* 选项变化处理逻辑，索引变化触发按钮文本以及所持有的CycleOption变化 */
        optionProperty.addListener((obs, oldVal, newVal) -> {
            getTextNode().textProperty().set(newVal.getText());
        });
    }

    public ObjectProperty<OptionItem<T>> optionProperty() {
        return optionProperty;
    }

    @Getter
    @AllArgsConstructor
    public static class OptionItem<T> {
        String text;
        T option;

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            OptionItem<?> that = (OptionItem<?>) o;
            return Objects.equals(option, that.option);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(option);
        }
    }
}
