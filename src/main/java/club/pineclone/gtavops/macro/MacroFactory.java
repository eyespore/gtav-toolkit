package club.pineclone.gtavops.macro;

import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.trigger.Trigger;
import lombok.Getter;

import java.util.UUID;

public class MacroFactory {

    private final MacroRegistry registry = MacroRegistry.getInstance();
    @Getter private static final MacroFactory instance = new MacroFactory();

    private MacroFactory() {}

    public UUID createSimpleMacro(Trigger trigger, Action action) {
        SimpleMacro macro = new SimpleMacro(trigger, action);
        /* 创建宏时，如果全局处于挂起状态，那么需要将新生的宏挂起 */
        if (MacroRegistry.isGlobalSuspended()) macro.suspend();
        return registry.register(macro);
    }

}
