package club.pineclone.gtavops.macro.trigger;


import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class TriggerEvent {

    private final Trigger source;
    private final boolean blocked;

    public TriggerEvent(final Trigger source) {
        this(source, false);
    }

    public TriggerEvent(final Trigger source, final boolean blocked) {
        this.source = source;
        this.blocked = blocked;
    }
}
