package club.pineclone.gtavops.macro.trigger;


import lombok.ToString;

@ToString
public class TriggerEvent {

    private final Trigger source;

    public TriggerEvent(final Trigger source) {
        this.source = source;
    }

    public Trigger getSource() {
        return source;
    }
}
