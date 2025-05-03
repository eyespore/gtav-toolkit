module club.pineclone.gtavops {
    requires javafx.web;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires jdk.jsobject;
    requires io.vproxy.vfx;
    requires com.github.kwhat.jnativehook;
    requires io.vproxy.base;
    requires com.fasterxml.jackson.databind;
    requires vjson;
    requires static lombok;

    opens club.pineclone.gtavops;
    opens club.pineclone.gtavops.utils;

    exports club.pineclone.gtavops;
    exports club.pineclone.gtavops.utils;

    /* for test */
    exports club.pineclone.gtavops.macro;
    exports club.pineclone.gtavops.macro.trigger;
    exports club.pineclone.gtavops.macro.action;
    exports club.pineclone.gtavops.config;
    exports club.pineclone.gtavops.i18n;
    exports club.pineclone.gtavops.macro.action.robot;
    exports club.pineclone.gtavops.macro.trigger.source;
    exports club.pineclone.gtavops.macro.trigger.policy;
}
