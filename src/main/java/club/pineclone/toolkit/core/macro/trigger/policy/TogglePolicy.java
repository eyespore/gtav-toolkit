package club.pineclone.toolkit.core.macro.trigger.policy;

import club.pineclone.toolkit.core.macro.trigger.TriggerStatus;
import club.pineclone.toolkit.core.macro.trigger.source.InputSourceEvent;

import java.util.Optional;
import java.util.function.Consumer;

public class TogglePolicy implements ActivationPolicy {
    private boolean toggled = false;

    @Override
    public void decide(InputSourceEvent event, Consumer<Optional<TriggerStatus>> callback) {
        switch (event.getOperation()) {
            case KEY_PRESSED, MOUSE_PRESSED -> {
                toggled = !toggled;
                TriggerStatus status = toggled ? TriggerStatus.TOGGLE_ON : TriggerStatus.TOGGLE_OFF;
                callback.accept(fire(status));
            }
            default -> callback.accept(Optional.empty());
        };
    }
}
