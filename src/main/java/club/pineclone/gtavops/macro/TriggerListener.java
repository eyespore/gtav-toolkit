package club.pineclone.gtavops.macro;

import club.pineclone.gtavops.macro.trigger.TriggerEvent;

public interface TriggerListener {

    void onTriggerActivate(TriggerEvent event);

    void onTriggerDeactivate(TriggerEvent event);

}
