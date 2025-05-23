package club.pineclone.gtavops.macro;

public interface MacroLifecycleAware {

    default void install() {}

    default void uninstall() {}

    default void suspend() {}

    default void resume() {}

}
