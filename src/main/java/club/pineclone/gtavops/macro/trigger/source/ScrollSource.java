package club.pineclone.gtavops.macro.trigger.source;

import club.pineclone.gtavops.utils.KeyUtils;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelListener;
import io.vproxy.vfx.entity.input.Key;


public class ScrollSource extends InputSource implements NativeMouseWheelListener {

    private final int vcScroll;

    public ScrollSource(final Key key) {
        this.vcScroll = KeyUtils.toVCScroll(key.scroll);
    }

    @Override
    public void nativeMouseWheelMoved(NativeMouseWheelEvent nativeEvent) {
        if (nativeEvent.getWheelDirection() == vcScroll) {
            listener.onInputSourceEvent(true);
        }
    }

    @Override
    public void install() {
        GlobalScreen.addNativeMouseWheelListener(this);
    }

    @Override
    public void uninstall() {
        GlobalScreen.removeNativeMouseWheelListener(this);
    }
}
