package club.pineclone.gtavops.gui.forked;

import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.i18n.I18nHolder;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelListener;
import io.vproxy.vfx.control.dialog.VDialog;
import io.vproxy.vfx.control.dialog.VDialogButton;
import io.vproxy.vfx.control.globalscreen.GlobalScreenUtils;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.entity.input.KeyCode;
import io.vproxy.vfx.entity.input.MouseWheelScroll;
import io.vproxy.vfx.manager.internal_i18n.InternalI18n;
import javafx.application.Platform;
import javafx.scene.input.MouseButton;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.github.kwhat.jnativehook.keyboard.NativeKeyEvent.*;
import static com.github.kwhat.jnativehook.keyboard.NativeKeyEvent.KEY_LOCATION_NUMPAD;


public class ForkedKeyChooser extends VDialog<Key> {

    private final int flags;

    private final KeyListener keyListener = new KeyListener();
    private final MouseListener mouseListener = new MouseListener();
    private final WheelScrollListener wheelScrollListener = new WheelScrollListener();

    public static final int FLAG_WITH_KEY = 0x0001;   // 1 << 0, 监听键盘
    public static final int FLAG_WITH_MOUSE = 0x0002;  // 1 << 1, 监听鼠标按键
    public static final int FLAG_WITH_WHEEL_SCROLL = 0x0004;  // 1 << 2, 监听鼠标滚轮

    public ForkedKeyChooser() {
        this(FLAG_WITH_KEY);  // 默认监听键盘
    }

    public ForkedKeyChooser(int flags) {
        this.flags = flags;

        List<VDialogButton<Key>> buttons = new ArrayList<>();
        ExtendedI18n i18n = I18nHolder.get();
        if ((flags & FLAG_WITH_MOUSE) == FLAG_WITH_MOUSE) {
            buttons.add(new VDialogButton<>(InternalI18n.get().keyChooserLeftMouseButton(), new Key(MouseButton.PRIMARY)));
            buttons.add(new VDialogButton<>(InternalI18n.get().keyChooserMiddleMouseButton(), new Key(MouseButton.MIDDLE)));
            buttons.add(new VDialogButton<>(InternalI18n.get().keyChooserRightMouseButton(), new Key(MouseButton.SECONDARY)));
            buttons.add(new VDialogButton<>(i18n.keyChooserForwardMouseButton, new Key(MouseButton.FORWARD)));
            buttons.add(new VDialogButton<>(i18n.keyChooserBackMouseButton, new Key(MouseButton.BACK)));
        }
        buttons.add(new VDialogButton<>(InternalI18n.get().cancelButton(), () -> null));
        setButtons(buttons);
        boolean withMouse = (flags & (FLAG_WITH_WHEEL_SCROLL | FLAG_WITH_MOUSE)) != 0;
        getMessageNode().setText(withMouse ? InternalI18n.get().keyChooserDesc() : InternalI18n.get().keyChooserDescWithoutMouse());

        if (buttons.size() > 4) {
            getStage().getStage().setWidth(1200);
        }

        List<String> listenedComponents = new ArrayList<>();

        if ((flags & FLAG_WITH_KEY) != 0) {
            listenedComponents.add(i18n.keyboard);
        }
        if ((flags & FLAG_WITH_MOUSE) != 0) {
            listenedComponents.add(i18n.mouseButton);
        }
        if ((flags & FLAG_WITH_WHEEL_SCROLL) != 0) {
            listenedComponents.add(i18n.mouseWheel);
        }

        getMessageNode().setText(MessageFormat.format(i18n.keyChooserDescription, String.join(",", listenedComponents)));
    }

    private void registerListeners(int flags) {
        if ((flags & FLAG_WITH_KEY) == FLAG_WITH_KEY) {
            GlobalScreen.addNativeKeyListener(keyListener);
        }
        /* 采用点击的方式选择鼠标按键，而不是监听，监听容易造成误触 */
//        if ((flags & FLAG_WITH_MOUSE) == FLAG_WITH_MOUSE) {
//            GlobalScreen.addNativeMouseListener(mouseListener);
//        }
        if ((flags & FLAG_WITH_WHEEL_SCROLL) == FLAG_WITH_WHEEL_SCROLL) {
            GlobalScreen.addNativeMouseWheelListener(wheelScrollListener);
        }
    }

    private void unregisterListeners(int flags) {
        if ((flags & FLAG_WITH_KEY) == FLAG_WITH_KEY) {
            GlobalScreen.removeNativeKeyListener(keyListener);
        }
//        if ((flags & FLAG_WITH_MOUSE) == FLAG_WITH_MOUSE) {
//            GlobalScreen.removeNativeMouseListener(mouseListener);
//        }
        if ((flags & FLAG_WITH_WHEEL_SCROLL) == FLAG_WITH_WHEEL_SCROLL) {
            GlobalScreen.removeNativeMouseWheelListener(wheelScrollListener);
        }
    }

    public Optional<Key> choose() {
        GlobalScreenUtils.enable(this);
        registerListeners(flags);
        var ret = showAndWait();
        unregisterListeners(flags);
        GlobalScreenUtils.disable(this);
        return ret;
    }

    private class KeyListener implements NativeKeyListener {
        @Override
        public void nativeKeyPressed(NativeKeyEvent e) {
            Key key;
            if (e.getKeyCode() == VC_CONTROL || e.getKeyCode() == VC_ALT || e.getKeyCode() == VC_SHIFT || e.getKeyCode() == 0x0e36 /*right shift*/) {
                boolean isLeft;
                if (e.getKeyLocation() == KEY_LOCATION_LEFT) {
                    isLeft = true;  // 左侧键修饰
                } else if (e.getKeyLocation() == KEY_LOCATION_RIGHT) {
                    isLeft = false;  // 右侧键修饰
                } else {
                    return; // should not happen, but if happens, we ignore this event
                }
                key = new Key(KeyCode.valueOf(e.getKeyCode()), isLeft);
            } else {
                if (e.getKeyLocation() == KEY_LOCATION_NUMPAD) {
                    return; // ignore numpad
                }
                key = new Key(KeyCode.valueOf(e.getKeyCode()));
                if (!key.isValid()) key = null;
            }

            Key finalKey = key;
            Platform.runLater(() -> {
                ForkedKeyChooser.this.returnValue = finalKey;
                ForkedKeyChooser.this.getStage().close();
            });
        }
    }

    private class MouseListener implements NativeMouseListener {
        @Override
        public void nativeMouseClicked(NativeMouseEvent e) {
            Key key;
            switch (e.getButton()) {
                case NativeMouseEvent.BUTTON1: {
                    key = new Key(MouseButton.PRIMARY); // 左键
                    break;
                }
                case NativeMouseEvent.BUTTON2: {
                    key = new Key(MouseButton.SECONDARY);  // 右键
                    break;
                }
                case NativeMouseEvent.BUTTON3: {
                    key = new Key(MouseButton.MIDDLE); // 中键
                    break;
                }
                case NativeMouseEvent.BUTTON4: {
                    key = new Key(MouseButton.BACK);  // 后退（侧键）
                    break;
                }
                case NativeMouseEvent.BUTTON5: {
                    key = new Key(MouseButton.FORWARD);  // 前进（侧键）
                    break;
                }
                default: return;  // 如果是其他鼠标按键，忽略
            }

            Platform.runLater(() -> {
                ForkedKeyChooser.this.returnValue = key;
                ForkedKeyChooser.this.getStage().close();
            });
        }
    }

    private class WheelScrollListener implements NativeMouseWheelListener {
        @Override
        public void nativeMouseWheelMoved(NativeMouseWheelEvent e) {
            Key key;
            int rotation = e.getWheelRotation();
            if (rotation < 0) {
                /* 鼠标向上滚动 */
                key = new Key(new MouseWheelScroll(MouseWheelScroll.Direction.UP));
            } else if (rotation > 0) {
                /* 鼠标向下滚动 */
                key = new Key(new MouseWheelScroll(MouseWheelScroll.Direction.DOWN));
            } else {
                key = null;
            }

            Platform.runLater(() -> {
                ForkedKeyChooser.this.returnValue = key;
                ForkedKeyChooser.this.getStage().close();
            });
        }
    }

}
