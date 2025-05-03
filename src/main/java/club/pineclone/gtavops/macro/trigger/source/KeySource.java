package club.pineclone.gtavops.macro.trigger.source;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import io.vproxy.vfx.entity.input.Key;

public class KeySource extends InputSource implements NativeKeyListener {

    private final int vcCode;

    public KeySource(Key key) {
        this.vcCode = key.key.code;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        if (nativeEvent.getKeyCode() == vcCode) {
            listener.onInputSourceEvent(true);
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
        if (nativeEvent.getKeyCode() == vcCode) {
            listener.onInputSourceEvent(false);
        }
    }

    @Override
    public void install() {
        GlobalScreen.addNativeKeyListener(this);
    }

    @Override
    public void uninstall() {
        GlobalScreen.removeNativeKeyListener(this);
    }
}
