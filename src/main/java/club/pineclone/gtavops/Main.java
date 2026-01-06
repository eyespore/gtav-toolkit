package club.pineclone.gtavops;

import club.pineclone.gtavops.common.JLibLocator;
import club.pineclone.gtavops.common.SingletonLock;
import club.pineclone.gtavops.config.MacroConfigLoader;
import club.pineclone.gtavops.client.FXLifecycleAware;
import club.pineclone.gtavops.client.MainFX;
import club.pineclone.gtavops.client.forked.ForkedDialog;
import club.pineclone.gtavops.client.theme.BaseTheme;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.i18n.I18nHolder;
import club.pineclone.gtavops.jni.PlatformFocusMonitor;
import club.pineclone.gtavops.macro.MacroRegistry;
import club.pineclone.gtavops.macro.action.ActionTaskManager;
import club.pineclone.gtavops.utils.PathUtils;
import com.github.kwhat.jnativehook.GlobalScreen;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.alert.StackTraceAlert;
import javafx.application.Application;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        Theme.setTheme(new BaseTheme());
        MainFX.addFXListener(new FXBootstrap());
        Application.launch(MainFX.class, args);
    }

    /* 主窗口初始化上下文 */
    private static class FXBootstrap implements FXLifecycleAware {
        /* 主窗口启动 */
        @Override
        public void onFXStart() throws Exception {
            Class.forName(PlatformFocusMonitor.class.getName());  /* 平台焦点监听 */

            PlatformFocusMonitor.addListener(MacroRegistry.getInstance());  /* 添加监听器 */

            Class.forName(ActionTaskManager.class.getName());  /* 宏任务调度 */

            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());  /* 停止 jnativehook 日志记录 */
            logger.setLevel(Level.OFF);
            logger.setUseParentHandlers(false);

            MacroRegistry.getInstance().registerNativeHook();  /* 注册 jnativehook 全局钩子 */
        }

        /* 主窗口初始化，在此处执行程序的初始化逻辑 */
        @Override
        public void onFXInit() throws Exception {
            try {
                I18nHolder.load();  /* 初始化本地化 */
            } catch (IOException e) {
                /* i18n文件错误 */
                StackTraceAlert.showAndWait(e);  /* 避免使用i18n */
                System.exit(1);  /* 本地化文件加载错误直接退出 */
                return;
            }

            try {
                PathUtils.initAppHome();
            } catch (IOException e) {
                /* 应用家目录初始化异常 */
                ForkedDialog<Integer> dialog = ForkedDialog.stackTraceDialog(e, ForkedDialog.CONFIRM);
                dialog.showAndWait();
                System.exit(1);  /* 家目录初始化失败直接退出 */
                return;
            }

            /* 获取单例锁 */
            if (!SingletonLock.lockInstance()) {
                /* 程序已经在运行 */
                // TODO: 将逻辑迁移到MainFX
                Exception e = new RuntimeException(I18nHolder.get().duplicatedAppInstanceRunning);
                ForkedDialog<Integer> dialog = ForkedDialog.stackTraceDialog(e, ForkedDialog.CONFIRM);
                dialog.showAndWait();
                System.exit(1);
                return;
            }

            Class.forName(JLibLocator.class.getName());  /* 加载本地库 */

            try {
                MacroConfigLoader.load();
            } catch (IOException e) {
                /* 配置初始化异常 */
                ExtendedI18n i18n = I18nHolder.get();
                ForkedDialog<Integer> dialog = ForkedDialog.stackTraceDialog(i18n.configFileLoadFailed, e);
                Optional<Integer> result = dialog.showAndWait();
                System.exit(1);
            }
        }

        /* 主窗口停止运行 */
        @Override
        public void onFXStop() throws Exception {
            MacroRegistry.getInstance().unregisterNativeHook();  /* 注销 jnativehook 全局钩子 */

            ActionTaskManager.shutdown();  /* 停止任务调度 */
            PlatformFocusMonitor.shutdown();  /* 停止焦点监听 */
            MacroConfigLoader.save();  /* 保存配置 */
        }
    }
}
