package club.pineclone.gtavops.macro.trigger;

import java.util.*;

/* 组合按键触发器 */
public class CompositeTrigger extends Trigger implements TriggerListener {

    private final List<Trigger> triggers;
    private final Set<Trigger> activeSet = new HashSet<>();
//    private final Map<Trigger, Long> lastActiveTime = new HashMap<>();

    private boolean isActive = false;
    private static final long TOLERANCE_MS = 100;

    public CompositeTrigger(final List<Trigger> triggers) {
        this.triggers = triggers;
        triggers.forEach(t -> {
            t.addListener(this);
//            lastActiveTime.put(t, 0L);
        });
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
        Trigger source = event.getSource();
        synchronized (activeSet) {
            activeSet.add(source);
//            lastActiveTime.put(source, System.currentTimeMillis());
            if (activeSet.size() == triggers.size() && !isActive) {
                isActive = true;
                activate();
            }
        }
    }

    @Override
    public void onTriggerDeactivate(TriggerEvent event) {
        Trigger source = event.getSource();
        synchronized (activeSet) {
//            long now = System.currentTimeMillis();
//            long lastActive = lastActiveTime.getOrDefault(source, 0L);
//            if (now - lastActive < TOLERANCE_MS) return;
            activeSet.remove(source);
            if (isActive && activeSet.size() < triggers.size()) {
                isActive = false;
                deactivate();
            }
        }
    }
}
