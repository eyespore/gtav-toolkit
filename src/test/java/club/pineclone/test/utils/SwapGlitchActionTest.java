package club.pineclone.test.utils;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import junit.framework.TestCase;

public class SwapGlitchActionTest extends TestCase {

    public void testMacro() throws NativeHookException, InterruptedException {
        GlobalScreen.registerNativeHook();

//        MacroExecutor executor = new MacroExecutor(
//                TriggerFactory.getTrigger(
//                        new TriggerIdentity(
//                                TriggerIdentity.TriggerType.MOUSE_BUTTON,
//                                TriggerIdentity.TriggerMode.HOLD,
//                                new Key(MouseButton.BACK))),
//                new SwapGlitchAction());

//        executor.install();
//        Thread.sleep(10000);
//        executor.uninstall();
//        GlobalScreen.unregisterNativeHook();

    }

}
