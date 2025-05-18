package club.pineclone.gtavops.macro.trigger.source;

import club.pineclone.gtavops.utils.KeyUtils;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelListener;
import io.vproxy.vfx.entity.input.Key;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/* 鼠标滚轮输入源 */
public class MouseScrollSource extends InputSource implements NativeMouseWheelListener {

    private final Map<Integer, Key> keys = new HashMap<>();

    public MouseScrollSource(final Key... keys) {
        Arrays.stream(keys).forEach(k -> this.keys.put(KeyUtils.toVCScroll(k.scroll), k));
    }

    public MouseScrollSource(Set<Key> keys) {
        keys.forEach(k -> this.keys.put(KeyUtils.toVCScroll(k.scroll), k));
    }

    @Override
    public void nativeMouseWheelMoved(NativeMouseWheelEvent nativeEvent) {
        int vcScroll = nativeEvent.getWheelDirection();
        if (keys.containsKey(vcScroll)) {
            listener.onInputSourceEvent(InputSourceEvent.of(InputSourceEvent.Operation.MOUSE_WHEEL_MOVED, keys.get(vcScroll)));
        }
    }
}
