package club.pineclone.gtavops.macro.trigger.policy;

public class ClickPolicy implements ActivationPolicy {
    @Override
    public int decide(boolean press) {
        return press? 1 : 0;  /* 仅按下的时候激活，抬起的时候不激活 */
    }
}
