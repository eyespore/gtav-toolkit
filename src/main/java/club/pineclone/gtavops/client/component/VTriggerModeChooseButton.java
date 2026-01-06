package club.pineclone.gtavops.client.component;

import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.i18n.I18nHolder;
import club.pineclone.gtavops.macro.trigger.TriggerMode;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.util.FXUtils;
import javafx.beans.property.*;
import javafx.geometry.Rectangle2D;

import java.util.*;

/* 触发模式选择器，供选择触发模式为单击触发、切换出发或按住触发 */
public class VTriggerModeChooseButton extends FusionButton {

    public static final int FLAG_WITH_TOGGLE = 0x0001;   // 1 << 0, 切换触发
    public static final int FLAG_WITH_HOLD = 0x0002;  // 1 << 1, 按住触发
    public static final int FLAG_WITH_CLICK = 0x0004;  // 1 << 2, 点击触发

    private final ObjectProperty<TriggerMode> triggerModeProperty = new SimpleObjectProperty<>();
    private final Map<TriggerMode, TriggerModeOptionItem> itemMap = new EnumMap<>(TriggerMode.class);
    private final List<TriggerMode> triggerModeOrder = new ArrayList<>();

    private int currentIndex = 0;

    public VTriggerModeChooseButton(int flags) {
        StringProperty textProperty = new SimpleStringProperty();
        textProperty.addListener((observable, oldValue, newValue) -> {
            Rectangle2D textBounds = FXUtils.calculateTextBounds(getTextNode());
            setPrefWidth(Math.max(textBounds.getWidth() + 40, 120));
        });
        setPrefHeight(40);

        triggerModeProperty.addListener((obs, oldVal, newVal) -> {
            TriggerModeOptionItem item = itemMap.get(newVal);
            currentIndex = triggerModeOrder.indexOf(newVal);
            textProperty.set(item.text);
        });

        getTextNode().textProperty().bind(textProperty);
        setOnMouseClicked(event -> {
            if (triggerModeOrder.size() == 1) return;
            currentIndex ++;
            if (currentIndex >= triggerModeOrder.size()) currentIndex = 0;
            triggerModeProperty.set(triggerModeOrder.get(currentIndex));
        });

        ExtendedI18n i18n = I18nHolder.get();

        if ((flags & FLAG_WITH_TOGGLE) != 0) {
            itemMap.put(TriggerMode.TOGGLE, new TriggerModeOptionItem(TriggerMode.TOGGLE, i18n.common.toggle));
            triggerModeOrder.add(TriggerMode.TOGGLE);
        }

        if ((flags & FLAG_WITH_HOLD) != 0) {
            itemMap.put(TriggerMode.HOLD, new TriggerModeOptionItem(TriggerMode.HOLD, i18n.common.hold));
            triggerModeOrder.add(TriggerMode.HOLD);
        }

        if ((flags & FLAG_WITH_CLICK) != 0) {
            itemMap.put(TriggerMode.CLICK, new TriggerModeOptionItem(TriggerMode.CLICK, i18n.common.click));
            triggerModeOrder.add(TriggerMode.CLICK);
        }

        if (!triggerModeOrder.isEmpty()) {
            triggerModeProperty.set(triggerModeOrder.get(0));
        }
    }

    public ObjectProperty<TriggerMode> triggerModeProperty() {
        return triggerModeProperty;
    }

    private record TriggerModeOptionItem(TriggerMode mode, String text) { }

}
