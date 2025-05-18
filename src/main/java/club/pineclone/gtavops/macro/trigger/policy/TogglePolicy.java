package club.pineclone.gtavops.macro.trigger.policy;

import club.pineclone.gtavops.macro.trigger.source.InputSourceEvent;

public class TogglePolicy implements ActivationPolicy {
    private boolean toggled = false;

    @Override
    public int decide(InputSourceEvent event) {
        return switch (event.getOperation()) {
            case KEY_PRESSED, MOUSE_PRESSED -> {
                toggled = !toggled;
                yield toggled ? 1 : 0;
            }
            default -> -1;
        };
    }
}
