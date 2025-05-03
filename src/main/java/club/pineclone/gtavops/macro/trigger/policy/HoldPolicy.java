package club.pineclone.gtavops.macro.trigger.policy;

public class HoldPolicy implements ActivationPolicy {
    @Override
    public int decide(boolean press) {
        return press ? 1 : -1;
    }
}
