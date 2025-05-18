package club.pineclone.gtavops.macro.trigger.source;

/* 信号源 */

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelListener;
import lombok.Setter;

/**
 * @see KeyboardSource
 */
public abstract class InputSource {

    private boolean installed = false;
    @Setter protected InputSourceListener listener;

    public void install() {
        if (installed) return;
        if (this instanceof NativeKeyListener) {
            GlobalScreen.addNativeKeyListener((NativeKeyListener) this);
        } else if (this instanceof NativeMouseListener) {
            GlobalScreen.addNativeMouseListener((NativeMouseListener) this);
        } else if (this instanceof NativeMouseWheelListener) {
            GlobalScreen.addNativeMouseWheelListener((NativeMouseWheelListener) this);
        } else return;
        installed = true;
    }

    public void uninstall() {
        if (!installed) return;
        if (this instanceof NativeKeyListener) {
            GlobalScreen.removeNativeKeyListener((NativeKeyListener) this);
        } else if (this instanceof NativeMouseListener) {
            GlobalScreen.removeNativeMouseListener((NativeMouseListener) this);
        } else if (this instanceof NativeMouseWheelListener) {
            GlobalScreen.removeNativeMouseWheelListener((NativeMouseWheelListener) this);
        } else return;
        installed = false;
    }
}
