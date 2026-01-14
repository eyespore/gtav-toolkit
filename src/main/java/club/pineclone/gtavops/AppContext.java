package club.pineclone.gtavops;

import club.pineclone.gtavops.common.JNativeHookManager;
import club.pineclone.gtavops.common.PathUtils;
import club.pineclone.gtavops.config.AppConfig;
import club.pineclone.gtavops.dao.MacroConfigDAO;
import club.pineclone.gtavops.dao.MacroEntryDAO;
import club.pineclone.gtavops.dao.impl.JsonMacroConfigDAO;
import club.pineclone.gtavops.core.jni.PlatformFocusMonitor;
import club.pineclone.gtavops.dao.impl.JsonMacroEntryDAO;
import club.pineclone.gtavops.domain.mapper.MacroConfigMapper;
import club.pineclone.gtavops.domain.mapper.MacroEntryMapper;
import club.pineclone.gtavops.service.MacroFactory;
import club.pineclone.gtavops.service.MacroRegistry;
import club.pineclone.gtavops.core.macro.MacroTaskScheduler;
import lombok.Getter;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class AppContext {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final List<AppLifecycleAware> listeners;
    private static volatile AppContext INSTANCE;  /* 单例模式 */

    private final AppConfig appConfig;  /* 应用内嵌配置 */
    private final MacroFactory macroFactory;  /* 宏工厂 */
    private final MacroRegistry macroRegistry;  /* 宏注册表 */

    private final PlatformFocusMonitor platformFocusMonitor;  /* 平台焦点监听器 */
    @Getter private final MacroTaskScheduler macroTaskScheduler;  /* 宏任务调度器 */  // TODO: 优化到宏内核，由父类封装方法

    private final MacroEntryMapper macroEntryMapper;  /* 面向 MacroEntry 的映射器 */
    private final MacroEntryDAO macroEntryDao;  /* 宏实例持久层 */

    private final MacroConfigMapper macroConfigMapper;  /* 面向 MacroConfig 的映射器 */
    private final MacroConfigDAO macroConfigDao;  /* 宏配置持久层 */

    public AppContext() {
        if (INSTANCE != null) throw new IllegalStateException("AppContext instance already exists.");
        this.listeners = new ArrayList<>();

        /* 工具实例化 */
        this.appConfig = new AppConfig(PathUtils.getAppHomePath());
        this.macroFactory = new MacroFactory();
        this.macroTaskScheduler = new MacroTaskScheduler();
        this.platformFocusMonitor = new PlatformFocusMonitor();
        this.macroRegistry = new MacroRegistry(platformFocusMonitor);

        /* MapStruct 实例化 */
        this.macroConfigMapper = Mappers.getMapper(MacroConfigMapper.class);
        this.macroEntryMapper = Mappers.getMapper(MacroEntryMapper.class);

        /* 持久层初始化 */
        this.macroEntryDao = new JsonMacroEntryDAO(
                appConfig.getJsonMacroDataStoreSettings().getMacroEntryPath(),
                macroEntryMapper);

        this.macroConfigDao = new JsonMacroConfigDAO(
                appConfig.getJsonMacroDataStoreSettings().getMacroConfigPath(),
                macroEntryDao,
                macroEntryMapper,
                macroConfigMapper
        );

        /* 底层资源生命周期注册 */
        registerLifecycleObj(macroEntryDao);
        registerLifecycleObj(macroConfigDao);

        /* 上层工具生命周期注册 */
        registerLifecycleObj(macroRegistry);
        registerLifecycleObj(macroTaskScheduler);
        registerLifecycleObj(platformFocusMonitor);

    }

    private void registerLifecycleObj(Object obj) {
        if (obj instanceof AppLifecycleAware) {
            this.listeners.add((AppLifecycleAware) obj);
        }
    }

    public static AppContext getInstance() {
        if (INSTANCE == null) {
            synchronized (AppContext.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppContext();
                }
            }
        }
        return INSTANCE;
    }

    public void init() throws Exception {
        log.info("Initializing macro core app home directory");  /* 初始化宏后端应用家目录 */
        Files.createDirectories(appConfig.getCoreHomePath());

        /* 注册 jnativehook 全局钩子在应用启动阶段调用，注册jnativehook全局监听钩子，从而确保后续所有InputSource监听器能够正常工作，*/
        log.info("Register jnativehook global native hook for macro core");
        JNativeHookManager.register(AppContext.class);

        for (AppLifecycleAware listener : listeners) {
            listener.onAppInit();
        }
    }

    public void start() throws Exception {
        for (AppLifecycleAware listener : listeners) {
            listener.onAppStart();
        }
    }

    public void stop() throws Exception {
        JNativeHookManager.unregister(AppContext.class); /* 注销 jnativehook 全局钩子 */
        for (AppLifecycleAware listener : listeners) {
            listener.onAppStop();
        }
    }
}
