package club.pineclone.test.utils;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.entity.input.KeyCode;
import junit.framework.TestCase;

public class KeyTest extends TestCase {

    public void testKey() {
        System.out.println((new Key(KeyCode.TAB)));
    }

}
