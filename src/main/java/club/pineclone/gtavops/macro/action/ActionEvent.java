package club.pineclone.gtavops.macro.action;

import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerEvent;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ActionEvent {

    private final TriggerEvent triggerEvent;

    public ActionEvent(TriggerEvent triggerEvent) {
        this.triggerEvent = triggerEvent;
    }

    /* TriggerEvent派生ActionEvent */
    public static ActionEvent of(TriggerEvent event) {
        return new ActionEvent(event);
    }
}
