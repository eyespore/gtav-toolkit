package club.pineclone.gtavops.macro.action.decorator;

import club.pineclone.gtavops.macro.action.ActionEvent;
import club.pineclone.gtavops.macro.action.robot.RobotFactory;
import club.pineclone.gtavops.macro.action.ScheduledAction;
import club.pineclone.gtavops.macro.action.ScheduledActionDecorator;
import club.pineclone.gtavops.macro.action.robot.VCRobotAdapter;
import io.vproxy.vfx.entity.input.Key;

public class SwapMeleeDecorator extends ScheduledActionDecorator {

    private final long delay;
    private final Key hotkey;  /* 切换近战武器热键 */
    private final VCRobotAdapter robot;  /* 机器人 */

    public SwapMeleeDecorator(ScheduledAction delegate, Key hotkey, long delay) {
        super(delegate);
        this.delay = delay;
        this.hotkey = hotkey;
        this.robot = RobotFactory.getRobot(delegate.getActionId());
    }

    /* 在动作(比如切枪偷速)开始之前，切换到近战武器 */
    @Override
    public boolean beforeActivate(ActionEvent event) throws Exception {
        if (!delegate.beforeActivate(event)) return false;

        robot.simulate(hotkey);
        Thread.sleep(10);
        robot.simulate(hotkey);
        Thread.sleep(delay);
        return true;

    }
}
