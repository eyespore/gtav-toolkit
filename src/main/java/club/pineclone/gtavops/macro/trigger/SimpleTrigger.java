package club.pineclone.gtavops.macro.trigger;

import club.pineclone.gtavops.macro.trigger.policy.ActivationPolicy;
import club.pineclone.gtavops.macro.trigger.source.InputSource;
import club.pineclone.gtavops.macro.trigger.source.InputSourceEvent;
import club.pineclone.gtavops.macro.trigger.source.InputSourceListener;

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
    public void install() {
        source.install();
    }

    @Override
    public void uninstall() {
        source.uninstall();
    }

    @Override
    public void onInputSourceEvent(InputSourceEvent event) {
        int flag = policy.decide(event);
        if (flag == 1) activate(TriggerEvent.ofNormal(this, event));  /* 执行 */
        else if (flag == 0) deactivate(TriggerEvent.ofNormal(this, event));  /* 拒绝 */
    }

    @Override
    public String toString() {
        return "SimpleTrigger{" +
                "policy=" + policy +
                ", source=" + source +
                '}';
    }
}
