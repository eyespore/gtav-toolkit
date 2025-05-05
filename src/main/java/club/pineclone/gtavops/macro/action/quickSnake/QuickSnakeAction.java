package club.pineclone.gtavops.macro.action.quickSnake;

import club.pineclone.gtavops.macro.action.ActionEvent;
import club.pineclone.gtavops.macro.action.robot.RobotFactory;
import club.pineclone.gtavops.macro.action.ScheduledAction;
import club.pineclone.gtavops.macro.action.robot.VCRobotAdapter;
import io.vproxy.vfx.entity.input.Key;

public class QuickSnakeAction extends ScheduledAction {

    private final Key snakeKey;
    private final VCRobotAdapter robot;

    /**
     * 轮盘吃零食宏，执行时会协助快速按下零食键，从而尽可能提升恢复血量的速度
     * @param interval 吃零食间隔
     * @param snakeKey 吃零食按键
     */
    public QuickSnakeAction(long interval, Key snakeKey) {
        super(interval);
        this.snakeKey = snakeKey;
        this.robot = RobotFactory.getRobot(snakeKey);
    }

    @Override
    public void schedule(ActionEvent event) throws Exception {
        robot.simulate(this.snakeKey);
    }
}
