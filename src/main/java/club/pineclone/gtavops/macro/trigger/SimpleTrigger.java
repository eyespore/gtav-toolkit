package club.pineclone.gtavops.macro.trigger;

import club.pineclone.gtavops.macro.trigger.policy.ActivationPolicy;
import club.pineclone.gtavops.macro.trigger.source.InputSource;
import club.pineclone.gtavops.macro.trigger.source.InputSourceEvent;
import club.pineclone.gtavops.macro.trigger.source.InputSourceListener;

import java.util.Optional;

/* 触发器 */
public class SimpleTrigger extends Trigger implements InputSourceListener {

    private final ActivationPolicy policy;
    private final InputSource source;

    public SimpleTrigger(InputSource source, ActivationPolicy policy) {
        this.policy = policy;
        this.source = source;
        source.setListener(this);
    }

    @Override
    public void onMarcoInstall() {
        this.source.onMarcoInstall();
        this.policy.onMarcoInstall();
    }

    @Override
    public void onMarcoUninstall() {
        this.policy.onMarcoUninstall();
        this.source.onMarcoUninstall();
    }

    @Override
    public void onInputSourceEvent(InputSourceEvent event) {
        policy.decide(event, o -> o.ifPresent(
                s -> super.fire(TriggerEvent.of(this, s, event))));
    }

    @Override
    public String toString() {
        return "SimpleTrigger{" +
                "policy=" + policy +
                ", source=" + source +
                '}';
    }
}
