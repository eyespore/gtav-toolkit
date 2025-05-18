package club.pineclone.gtavops.macro.trigger;


import club.pineclone.gtavops.macro.trigger.source.InputSourceEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class TriggerEvent {

    private Trigger source;
    private TriggerStatus triggerStatus;
    private InputSourceEvent inputSourceEvent;

    private TriggerEvent(final Trigger source, TriggerStatus triggerStatus, InputSourceEvent inputSourceEvent) {
        this.source = source;
        this.triggerStatus = triggerStatus;
        this.inputSourceEvent = inputSourceEvent;
    }

    public static TriggerEvent ofNormal(Trigger source, InputSourceEvent inputSourceEvent) {
        return new TriggerEvent(source, TriggerStatus.NORMAL, inputSourceEvent);
    }

    public static TriggerEvent ofBlocked(Trigger source, InputSourceEvent inputSourceEvent) {
        return new TriggerEvent(source, TriggerStatus.BLOCKED, inputSourceEvent);
    }
}
