package club.pineclone.gtavops.macro;

public interface MacroContextHolder {

    MacroRegistry MACRO_REGISTRY = MacroRegistry.getInstance();
    MacroFactory MACRO_FACTORY = MacroFactory.getInstance();

}
