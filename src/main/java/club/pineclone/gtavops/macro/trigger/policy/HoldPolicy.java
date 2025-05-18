package club.pineclone.gtavops.macro.trigger.policy;

import club.pineclone.gtavops.macro.trigger.source.InputSourceEvent;

public class HoldPolicy implements ActivationPolicy {
    @Override
    public int decide(InputSourceEvent event) {
        return switch (event.getOperation()) {
            case MOUSE_PRESSED, KEY_PRESSED -> 1;  /* 按键按下 */
            case MOUSE_RELEASED, KEY_RELEASED -> 0;  /* 按键抬起 */
            default -> -1;  /* 默认忽略 */
        };
    }
}
