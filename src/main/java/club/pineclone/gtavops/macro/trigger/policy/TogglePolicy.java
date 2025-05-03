package club.pineclone.gtavops.macro.trigger.policy;

public class TogglePolicy implements ActivationPolicy {
    private boolean state = false;

    @Override
    public int decide(boolean press) {
        if (press) {
            state = !state;
            return state ? 1 : -1;
        }
        return 0;
    }
}
