package club.pineclone.gtavops.macro.trigger.policy;

import club.pineclone.gtavops.macro.trigger.source.InputSourceEvent;

public class ClickPolicy implements ActivationPolicy {
    @Override
    public int decide(InputSourceEvent event) {
        return switch (event.getOperation()) {
            case KEY_PRESSED, MOUSE_PRESSED, MOUSE_WHEEL_MOVED -> 1;
            case KEY_RELEASED, MOUSE_RELEASED -> -1;  /* 忽略 */
        };
    }
}
