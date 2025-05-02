package club.pineclone.gtavops.macro.trigger;

import club.pineclone.gtavops.macro.TriggerListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* 组合按键触发器 */
public class KeyChordTrigger extends Trigger implements TriggerListener {

    private final List<Trigger> triggers;
    private final Set<Trigger> activeSet = new HashSet<>();
    private boolean isActive = false;

    public KeyChordTrigger(final List<Trigger> triggers) {
        this.triggers = triggers;
        triggers.forEach(t -> t.addListener(this));
    }

    @Override
    public void install() {
        triggers.forEach(Trigger::install);
    }

    @Override
    public void uninstall() {
        triggers.forEach(Trigger::uninstall);
    }

    @Override
    public void onTriggerActivate(TriggerEvent event) {
        synchronized (activeSet) {
            System.out.println("activate" + event);
            activeSet.add(event.getSource());
            if (activeSet.size() == triggers.size() && !isActive) {
                isActive = true;
                fireActivate();
            }
        }
    }

    @Override
    public void onTriggerDeactivate(TriggerEvent event) {
        synchronized (activeSet) {
            System.out.println("deactivate" + event);
            activeSet.remove(event.getSource());
            if (isActive && activeSet.size() < triggers.size()) {
                isActive = false;
                fireDeactivate();
            }
        }
    }
}
