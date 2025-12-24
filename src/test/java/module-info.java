module club.pineclone.test {
    requires club.pineclone.gtavops;
    requires com.fasterxml.jackson.databind;
    requires io.vproxy.vfx;
    requires junit;
    requires com.github.kwhat.jnativehook;

    requires static lombok;
    requires io.vproxy.base;
    requires javafx.graphics;

    opens club.pineclone.test.utils;
    opens club.pineclone.test.context;
    opens club.pineclone.test.action;
}