package club.pineclone.gtavops.macro.trigger;

import club.pineclone.gtavops.macro.TriggerListener;

import java.util.ArrayList;
import java.util.List;

public abstract class Trigger {

    private final List<TriggerListener> listeners = new ArrayList<>();

    /* 触发器执行 */
    protected void fireActivate() {
        TriggerEvent event = new TriggerEvent(this);
        listeners.forEach(l -> l.onTriggerActivate(event));
    }

    /* 触发器停止 */
    protected void fireDeactivate() {
        TriggerEvent event = new TriggerEvent(this);
        listeners.forEach(l -> l.onTriggerDeactivate(event));
    }

    /* 添加监听器 */
    public void addListener(TriggerListener listener) {
        listeners.add(listener);
    }

    /* 移除监听器 */
    public void removeListener(TriggerListener listener) {
        listeners.remove(listener);
    }

    public abstract void install();

    public abstract void uninstall();

}
