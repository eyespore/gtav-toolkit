package club.pineclone.gtavops.macro.trigger;

public class ConditionalTrigger extends Trigger implements TriggerListener {

    private final Trigger main;  /* 主触发器 */
    private final Trigger blocker;  /* 屏蔽触发器 */

    private boolean mainActive = false;  /* 主触发器执行 */
    private boolean blockerActive = false;  /* 屏蔽触发器执行 */

    /* 条件触发器，如果block被按下，那么main不会触发 */
    public ConditionalTrigger(Trigger main, Trigger blocker) {
        this.main = main;
        this.blocker = blocker;

        main.addListener(this);
        blocker.addListener(this);
    }

    @Override
    public void install() {
        main.install();
        blocker.install();
    }

    @Override
    public void uninstall() {
        main.uninstall();
        blocker.uninstall();
    }

    @Override
    public void onTriggerActivate(TriggerEvent event) {
        if (event.getSource() == main) {
            mainActive = true;
            if (!blockerActive) activate(new TriggerEvent(this, false));
        } else if (event.getSource() == blocker) {
            blockerActive = true;
            if (mainActive) deactivate(new TriggerEvent(this, true));  /* 主键触发，组织主键 */
        }
    }

    @Override
    public void onTriggerDeactivate(TriggerEvent event) {
        if (event.getSource() == main) {  /* 主键被松开 */
            mainActive = false;
            if (!blockerActive) {
                deactivate(new TriggerEvent(this, false));
            }
        } else if (event.getSource() == blocker) {
            blockerActive = false;
            if (mainActive) {
                activate(new TriggerEvent(this, false));  /* 松开屏蔽器时主动作被恢复 */
            }
        }
    }

    @Override
    protected TriggerEvent createTriggerEvent() {
        return new TriggerEvent(this, blockerActive);
    }
}
