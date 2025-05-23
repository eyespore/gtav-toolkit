package club.pineclone.gtavops.common;

import club.pineclone.gtavops.config.ConfigHolder;
import club.pineclone.gtavops.config.Config;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.i18n.I18nHolder;

public interface ResourceHolder {

    default ExtendedI18n getI18n() {
        return I18nHolder.get();
    }

    default Config getConfig() {
        return ConfigHolder.get();
    }
}
