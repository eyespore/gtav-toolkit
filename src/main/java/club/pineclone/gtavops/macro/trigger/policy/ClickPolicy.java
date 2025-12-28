package club.pineclone.gtavops.macro.trigger.policy;

import club.pineclone.gtavops.macro.trigger.TriggerStatus;
import club.pineclone.gtavops.macro.trigger.source.InputSourceEvent;

import java.util.Optional;
import java.util.function.Consumer;

public class ClickPolicy implements ActivationPolicy {

    @Override
    public void decide(InputSourceEvent event, Consumer<Optional<TriggerStatus>> callback) {
        switch (event.getOperation()) {
            case KEY_PRESSED, MOUSE_PRESSED, MOUSE_WHEEL_MOVED -> callback.accept(fire(TriggerStatus.CLICK));
            default -> callback.accept(Optional.empty());
        };
    }

}
