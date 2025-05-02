module club.pineclone.test {
    requires club.pineclone.gtavops;
    requires com.fasterxml.jackson.databind;
    requires io.vproxy.vfx;
    requires junit;
    requires com.github.kwhat.jnativehook;

    requires static lombok;
    requires io.vproxy.base;

    opens club.pineclone.test.utils;
}