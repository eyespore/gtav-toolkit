package club.pineclone.gtavops.macro.swapglitch;

import club.pineclone.gtavops.macro.action.RobotFactory;
import club.pineclone.gtavops.macro.action.ScheduledAction;
import club.pineclone.gtavops.macro.action.ScheduledActionDecorator;
import club.pineclone.gtavops.macro.action.VCRobotAdapter;
import io.vproxy.vfx.entity.input.Key;
import javafx.scene.input.MouseButton;

public class SwapRangedDecorator extends ScheduledActionDecorator {

    private final Key hotkey;  /* 切换近战武器热键 */
    private final VCRobotAdapter robot1;  /* 机器人 */
    private final VCRobotAdapter robot2;

    public SwapRangedDecorator(ScheduledAction delegate, Key hotkey) {
        super(delegate);
        this.hotkey = hotkey;
        this.robot1 = RobotFactory.getRobot(hotkey);
        this.robot2 = RobotFactory.getRobot(new Key(MouseButton.PRIMARY));
    }

    @Override
    public void afterSchedule() throws Exception {
        /* 尝试切换远程 */
        delegate.afterSchedule();
        Thread.sleep(20);
        robot1.simulate(hotkey);  /* 切换到枪 */
        Thread.sleep(20);
        robot2.simulate(new Key(MouseButton.PRIMARY));  /* 点左键选择 */
    }
}
