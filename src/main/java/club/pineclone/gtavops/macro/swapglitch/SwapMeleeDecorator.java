package club.pineclone.gtavops.macro.swapglitch;

import club.pineclone.gtavops.macro.action.RobotFactory;
import club.pineclone.gtavops.macro.action.ScheduledAction;
import club.pineclone.gtavops.macro.action.ScheduledActionDecorator;
import club.pineclone.gtavops.macro.action.VCRobotAdapter;
import io.vproxy.vfx.entity.input.Key;

public class SwapMeleeDecorator extends ScheduledActionDecorator {
    private final Key hotkey;  /* 切换近战武器热键 */
    private final VCRobotAdapter robot;  /* 机器人 */

    public SwapMeleeDecorator(ScheduledAction delegate, Key hotkey) {
        super(delegate);
        this.hotkey = hotkey;
        this.robot = RobotFactory.getRobot(hotkey);
    }

    @Override
    public void beforeSchedule() throws Exception {
        /* 尝试切换到近战 */
        delegate.beforeSchedule();
        robot.simulate(hotkey);
        Thread.sleep(10);
        robot.simulate(hotkey);
        Thread.sleep(120);
    }
}
