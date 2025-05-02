package club.pineclone.test.utils;

import club.pineclone.gtavops.macro.TriggerIdentity;
import club.pineclone.gtavops.macro.trigger.TriggerMode;
import club.pineclone.gtavops.macro.trigger.TriggerType;
import io.vproxy.vfx.entity.input.Key;
import junit.framework.TestCase;

public class KeyUtilsTest extends TestCase {


    public void testTriggerIdentity() {
        TriggerIdentity identity = new TriggerIdentity(
                TriggerType.KEYBOARD,
                TriggerMode.TOGGLE, new Key("Tab"));



    }

}
