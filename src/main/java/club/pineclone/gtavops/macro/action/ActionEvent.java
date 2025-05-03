package club.pineclone.gtavops.macro.action;

import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerEvent;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ActionEvent {

    private final Trigger source;
    private final boolean blocked;

    public ActionEvent(Trigger source, boolean blocked) {
        this.source = source;
        this.blocked = blocked;
    }

    public static ActionEvent of(Trigger source, boolean blocked) {
        return new ActionEvent(source, blocked);
    }

    /* TriggerEvent派生ActionEvent */
    public static ActionEvent of(TriggerEvent event) {
        return ActionEvent.of(event.getSource(), event.isBlocked());
    }

}
