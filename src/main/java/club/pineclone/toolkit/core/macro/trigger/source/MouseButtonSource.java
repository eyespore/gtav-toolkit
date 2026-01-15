package club.pineclone.toolkit.core.macro.trigger.source;

import club.pineclone.toolkit.common.KeyUtils;
import club.pineclone.toolkit.core.macro.input.Key;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/* 鼠标输入源 */
public class MouseButtonSource extends InputSource implements NativeMouseListener {

    private final Map<Integer, Key> keys = new HashMap<>();

    public MouseButtonSource(Key... keys) {
        Arrays.stream(keys).forEach(k -> this.keys.put(KeyUtils.toVCMouse(k.button), k));
    }

    public MouseButtonSource(Set<Key> keys) {
        keys.forEach(k -> this.keys.put(KeyUtils.toVCMouse(k.button), k));
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeEvent) {
        int vcMouse = nativeEvent.getButton();
        if (keys.containsKey(vcMouse)) {
            listener.onInputSourceEvent(InputSourceEvent.of(InputSourceEvent.Operation.MOUSE_PRESSED, keys.get(vcMouse)));
        }
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeEvent) {
        int vcMouse = nativeEvent.getButton();
        if (keys.containsKey(vcMouse)) {
            listener.onInputSourceEvent(InputSourceEvent.of(InputSourceEvent.Operation.MOUSE_RELEASED, keys.get(vcMouse)));
        }
    }
}
