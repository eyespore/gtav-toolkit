package club.pineclone.gtavops.macro.restorestrengthen;

import club.pineclone.gtavops.macro.action.RobotFactory;
import club.pineclone.gtavops.macro.action.ScheduledAction;
import club.pineclone.gtavops.macro.action.VCRobotAdapter;
import io.vproxy.vfx.entity.input.Key;

public class QuickSnakeAction extends ScheduledAction {

    private final Key snakeKey;
    private final VCRobotAdapter robot;

    /**
     * 轮盘吃零食宏
     * @param interval 吃零食间隔
     * @param snakeKey 吃零食按键
     */
    public QuickSnakeAction(long interval, Key snakeKey) {
        super(interval);
        this.snakeKey = snakeKey;
        this.robot = RobotFactory.getRobot(snakeKey);
    }

    @Override
    public void schedule() throws Exception {
        robot.simulate(this.snakeKey);
    }
}
