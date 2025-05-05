package club.pineclone.gtavops.macro.action.meleeGlitch;

import club.pineclone.gtavops.macro.action.ActionEvent;
import club.pineclone.gtavops.macro.action.ScheduledAction;
import club.pineclone.gtavops.macro.action.robot.RobotFactory;
import club.pineclone.gtavops.macro.action.robot.VCRobotAdapter;
import io.vproxy.vfx.entity.input.Key;

public class MeleeGlitchAction extends ScheduledAction {

    private final Key meleeSnakeScrollKey;  /* 近战武器零食滚轮键 */
    private final VCRobotAdapter robot;  /* 执行机器人 */

    /* 近战偷速宏 */
    public MeleeGlitchAction(long interval, Key meleeSnakeScrollKey) {
        super(interval);
        this.meleeSnakeScrollKey = meleeSnakeScrollKey;
        this.robot = RobotFactory.getRobot(meleeSnakeScrollKey);
    }

    @Override
    public void schedule(ActionEvent event) throws Exception {
        robot.simulate(meleeSnakeScrollKey);
    }
}
