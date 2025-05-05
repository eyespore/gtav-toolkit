package club.pineclone.gtavops.macro.action.swapglitch;

import club.pineclone.gtavops.macro.action.ActionEvent;
import club.pineclone.gtavops.macro.action.robot.RobotFactory;
import club.pineclone.gtavops.macro.action.ScheduledAction;
import club.pineclone.gtavops.macro.action.ScheduledActionDecorator;
import club.pineclone.gtavops.macro.action.robot.VCRobotAdapter;
import io.vproxy.vfx.entity.input.Key;
import javafx.scene.input.MouseButton;

public class SwapRangedDecorator extends ScheduledActionDecorator {

    private final Key hotkey;  /* 切换近战武器热键 */
    private final VCRobotAdapter robot1;  /* 机器人 */
    private final VCRobotAdapter robot2;

    private long safetyWeaponWheelStart;  /* 起始时间 */
    private final long safetyWeaponWheelDuration;

    public SwapRangedDecorator(ScheduledAction delegate, Key hotkey) {
        this(delegate, hotkey, 500);
    }

    public SwapRangedDecorator(ScheduledAction delegate, Key hotkey, long safetyWeaponWheelDuration) {
        super(delegate);
        this.hotkey = hotkey;
        this.robot1 = RobotFactory.getRobot(hotkey);
        this.robot2 = RobotFactory.getRobot(new Key(MouseButton.PRIMARY));
        this.safetyWeaponWheelDuration = safetyWeaponWheelDuration;
    }

    @Override
    public boolean beforeSchedule(ActionEvent event) throws Exception {
        if (!super.beforeSchedule(event)) {
            return false;
        }
        /* 保险有效期(ms) */
        return System.currentTimeMillis() - safetyWeaponWheelStart >= safetyWeaponWheelDuration;
    }

    @Override
    public void afterSchedule(ActionEvent event) throws Exception {
        /* 尝试切换远程 */
        delegate.afterSchedule(event);
        if (!event.isBlocked()) {
            Thread.sleep(20);
            robot1.simulate(hotkey);  /* 切换到枪 */
            Thread.sleep(20);
            robot2.simulate(new Key(MouseButton.PRIMARY));  /* 点左键选择 */
        } else {
            /* 被阻断，记录下阻塞开始时间 */
            safetyWeaponWheelStart = System.currentTimeMillis();
        }
    }
}
