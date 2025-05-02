package club.pineclone.gtavops.macro.trigger;

import club.pineclone.gtavops.utils.KeyUtils;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import io.vproxy.vfx.entity.input.Key;

public class MouseSource extends InputSource implements NativeMouseListener {

    private final int vcMouse;

    public MouseSource(Key key) {
        this.vcMouse = KeyUtils.toVCMouse(key.button);
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeEvent) {
        if (nativeEvent.getButton() == vcMouse) {
            listener.onInputSourceEvent(true);
        }
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeEvent) {
        if (nativeEvent.getButton() == vcMouse) {
            listener.onInputSourceEvent(false);
        }
    }

    @Override
    public void install() {
        GlobalScreen.addNativeMouseListener(this);
    }

    @Override
    public void uninstall() {
        GlobalScreen.removeNativeMouseListener(this);
    }
}
