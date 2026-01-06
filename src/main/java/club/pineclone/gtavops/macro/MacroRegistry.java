package club.pineclone.gtavops.macro;

import club.pineclone.gtavops.config.MacroConfig;
import club.pineclone.gtavops.jni.WindowTitleListener;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class MacroRegistry implements WindowTitleListener {

    @Getter private static final MacroRegistry instance = new MacroRegistry();
    @Getter private static volatile boolean globalSuspended = false;
    private static final String GTAV_WINDOW_TITLE = "Grand Theft Auto V";  /* 增强 & 传承标题相同 */

    private final Map<UUID, Macro> registry = new LinkedHashMap<>();
    private final Logger log = LoggerFactory.getLogger(MacroRegistry.class);

    /* 注：下面两个方法不应该多次调用，应该仅在应用的主生命周期中调用两次 */

    /**
     * 在应用启动阶段调用，注册jnativehook全局监听钩子，从而确保后续所有InputSource监听器能够正常工作，以及Action能够
     * 正常模拟操作
     */
    public void registerNativeHook() throws NativeHookException {
        GlobalScreen.registerNativeHook();
    }

    /**
     * 在应用终止阶段调用，销毁jnativehook全局监听钩子
     */
    public void unregisterNativeHook() throws NativeHookException {
        GlobalScreen.unregisterNativeHook();
        GlobalScreen.setEventDispatcher(null);
    }

    /* 宏注册入口，基于给定的配置和策略创建宏 */
    public UUID register(MacroConfig config, MacroCreationStrategies.MacroCreationStrategy strategy) {
        UUID uuid = UUID.randomUUID();
        Macro macro = strategy.apply(config);
        registry.put(uuid, macro);
        return uuid;
    }

    /* 启用某个宏，通常由GUI中的开关直接控制 */
    public boolean launchMacro(UUID uuid) {
        Macro macro = registry.get(uuid);
        if (macro == null) return false;
        macro.launch();
        return true;
    }

    /* 停止某个宏 */
    public boolean terminateMacro(UUID uuid) {
        Macro macro = registry.get(uuid);
        if (macro == null) return false;
        macro.terminate();
        registry.remove(uuid);
        return true;
    }

    /* 挂起所有的宏，GTA OPS通过监听当前用户焦点窗口判断用户是否在游戏内，当用户切出游戏时会将所有的宏挂起 */
    public void suspendAllMacros() {
        globalSuspended = true;
        registry.values().forEach(Macro::suspend);
    }

    /* 恢复所有的宏 */
    public void resumeAllMacros() {
        globalSuspended = false;
        registry.values().forEach(Macro::resume);
    }

    @Override
    public void accept(String s) {
        if (s.equals(GTAV_WINDOW_TITLE)) {
            resumeAllMacros();  /* 用户切回游戏 */
        } else {
            suspendAllMacros();  /* 用户切出游戏 */
        }
    }
}
