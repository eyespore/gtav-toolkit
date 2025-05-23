package club.pineclone.gtavops.macro;

import club.pineclone.gtavops.common.AbstractRegistry;
import club.pineclone.gtavops.jni.WindowTitleListener;
import lombok.Getter;

import java.util.UUID;

public class MacroRegistry
        extends AbstractRegistry<Macro>
        implements WindowTitleListener {

    @Getter private static final MacroRegistry instance = new MacroRegistry();
    @Getter private static volatile boolean globalSuspended = false;

    private static final String GTAV_WINDOW_TITLE = "Grand Theft Auto V";  /* 增强 & 传承标题相同 */

    public boolean install(UUID uuid) {
        Macro macro = get(uuid);
        if (macro == null) return false;
        macro.install();
        return true;
    }

    public boolean uninstall(UUID uuid) {
        Macro macro = unregister(uuid);
        if (macro == null) return false;
        macro.uninstall();
        return true;
    }

    public void suspendAll() {
        globalSuspended = true;
        values().forEach(Macro::suspend);
    }

    public void resumeAll() {
        globalSuspended = false;
        values().forEach(Macro::resume);
    }

    public void installAll() {
        values().forEach(Macro::install);
    }

    public void uninstallAll() {
        values().forEach(Macro::uninstall);
    }

    @Override
    public void accept(String s) {
        if (s.equals(GTAV_WINDOW_TITLE)) {
            resumeAll();  /* 用户切回游戏 */
        } else {
            suspendAll();  /* 用户切出游戏 */
        }
    }
}
