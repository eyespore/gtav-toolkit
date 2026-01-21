package club.pineclone.toolkit.core.macro;

import lombok.Getter;

/**
 * 宏上下文类
 */
public class MacroContext {

    private static volatile MacroContext INSTANCE;

    private final PlatformFocusMonitor platformFocusMonitor;  /* 平台焦点监听 */
    private final MacroRegistry macroRegistry;  /* 宏注册中心 */
    private final MacroFactory macroFactory;  /* 宏创建工厂 */

    @Getter private final MacroTaskScheduler scheduler;

    private MacroContext() {
        this.scheduler = new MacroTaskScheduler();
        this.platformFocusMonitor = new PlatformFocusMonitor();
        this.macroFactory = new MacroFactory();
        this.macroRegistry = new MacroRegistry(platformFocusMonitor);
    }

    public static MacroContext getInstance() {
        if (INSTANCE == null) {
            synchronized (MacroContext.class) {
                if (INSTANCE == null) INSTANCE = new MacroContext();
            }
        }
        return INSTANCE;
    }

    public void init() {
    }

    public void start() {
        platformFocusMonitor.start();
    }

    public void stop() {
        scheduler.shutdown();  /* 停止任务调度 */
        platformFocusMonitor.stop();  /* 停止平台焦点监听 */
    }
}
