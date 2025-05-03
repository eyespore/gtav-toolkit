package club.pineclone.gtavops.macro.action.adswing;

import club.pineclone.gtavops.macro.action.robot.RobotFactory;
import club.pineclone.gtavops.macro.action.ScheduledAction;
import club.pineclone.gtavops.macro.action.robot.VCRobotAdapter;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.entity.input.KeyCode;


public class ADSwingAction extends ScheduledAction {

    private final VCRobotAdapter robot1;
    private final VCRobotAdapter robot2;

    private final Key moveLeftKey;
    private final Key moveRightKey;

    private boolean pressLeft = false;

    /**
     * AD摇宏，执行宏的时候会依据设定的时间interval交替按下AD(可自定义)，从而实现在dc、近战偷速、
     * 轮盘回血时不丢失地速的效果
     * @param interval 触发AD的间隔
     * @param moveLeftKey 左移动键
     * @param moveRightKey 右移动键
     */
    public ADSwingAction(long interval, Key moveLeftKey, Key moveRightKey) {
        super(interval);
        this.moveLeftKey = moveLeftKey;
        this.moveRightKey = moveRightKey;

        this.robot1 = RobotFactory.getRobot(moveLeftKey);
        this.robot2 = RobotFactory.getRobot(moveRightKey);
    }


    @Override
    public void schedule() throws Exception {
        if (pressLeft) {
            pressLeft = false;
            robot1.simulate(moveLeftKey);
        } else {
            pressLeft = true;
            robot2.simulate(moveRightKey);
        }
    }
}
