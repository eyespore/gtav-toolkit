package club.pineclone.gtavops.common;

import club.pineclone.gtavops.config.MacroConfigLoader;
import club.pineclone.gtavops.config.MacroConfig;

// TODO: 销毁该接口，改用构造器注入
@Deprecated
public interface ResourceHolder {
    @Deprecated
    default MacroConfig getConfig() {
        return MacroConfigLoader.get();
    }
}
