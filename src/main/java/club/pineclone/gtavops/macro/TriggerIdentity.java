package club.pineclone.gtavops.macro;

import club.pineclone.gtavops.macro.trigger.TriggerMode;
import club.pineclone.gtavops.macro.trigger.TriggerType;
import io.vproxy.vfx.entity.input.Key;

import java.util.Objects;

public class TriggerIdentity {
    private final TriggerType type;
    private final TriggerMode mode;
    private final Key key;

    public TriggerIdentity(TriggerType type, TriggerMode mode, Key key) {
        if (!checkCompatibility(type, mode, key)) {
            throw new IllegalArgumentException();
        }
        this.type = type;
        this.mode = mode;
        this.key = key;
    }

    public TriggerIdentity(Key key, TriggerMode mode) {
        this(TriggerType.of(key), mode, key);
    }

    private boolean checkCompatibility(TriggerType type, TriggerMode mode, Key key) {
        return switch (type) {
            case KEYBOARD -> key.key != null && key.button == null && key.scroll == null;
            case MOUSE_BUTTON -> key.button != null && key.key == null && key.scroll == null;
            case SCROLL_WHEEL -> {
                // 确保滚轮仅仅能和Toggle组合，无法和Hold组合
                if (key.scroll != null && key.key == null && key.button == null) {
                    if (mode != TriggerMode.TOGGLE) {
                        throw new IllegalArgumentException("Mouse wheel can only work with TriggerMode.TOGGLE.");
                    }
                    yield true;
                }
                yield false;
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TriggerIdentity that = (TriggerIdentity) o;
        return type == that.type && mode == that.mode && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, mode, key);
    }

    public TriggerType getType() {
        return type;
    }

    public TriggerMode getMode() {
        return mode;
    }

    public Key getKey() {
        return key;
    }
}
