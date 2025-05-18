package club.pineclone.gtavops.macro;

public interface MacroLifecycleAware {

    default void install() {}

    default void uninstall() {}

}
