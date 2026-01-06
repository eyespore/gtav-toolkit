package club.pineclone.gtavops.macro.action.impl.swapglitch;

import club.pineclone.gtavops.macro.MacroEvent;
import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.action.ActionDecorator;
import club.pineclone.gtavops.macro.action.robot.RobotFactory;
import club.pineclone.gtavops.macro.action.robot.VCRobotAdapter;
import io.vproxy.vfx.entity.input.Key;

public class SwapMeleeAction extends ActionDecorator {

    private final long delay;
    private final Key hotkey;  /* 切换近战武器热键 */
    private final VCRobotAdapter robot;  /* 机器人 */

    public SwapMeleeAction(Action delegate, Key hotkey, long delay) {
        super(delegate);
        this.delay = delay;
        this.hotkey = hotkey;
        this.robot = RobotFactory.getRobot(delegate.getActionId());
    }

    /* 在动作(比如切枪偷速)开始之前，切换到近战武器 */
    @Override
    public boolean beforeActivate(MacroEvent event) {
        try {
            if (!delegate.beforeActivate(event)) return false;

            robot.simulate(hotkey);
            Thread.sleep(10);
            robot.simulate(hotkey);
            Thread.sleep(delay);
            return true;
        } catch (InterruptedException e) {
            /* 被中断时返回 false */
            return false;
        }
    }
}
