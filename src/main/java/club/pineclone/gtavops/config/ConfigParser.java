package club.pineclone.gtavops.config;

import club.pineclone.gtavops.macro.trigger.TriggerMode;

public class ConfigParser {
    @Deprecated
    public static TriggerMode parseTriggerMode(int configValue) {
        return configValue == 0 ? TriggerMode.HOLD : TriggerMode.TOGGLE;  /* 激活模式 切换执行 or 按住执行 */
    }
}
