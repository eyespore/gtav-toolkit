package club.pineclone.toolkit.core.macro.action.impl;

import club.pineclone.toolkit.core.macro.MacroEvent;
import club.pineclone.toolkit.core.macro.action.ScheduledAction;
import club.pineclone.toolkit.core.macro.action.robot.RobotFactory;
import club.pineclone.toolkit.core.macro.action.robot.VCRobotAdapter;
import club.pineclone.toolkit.core.macro.input.Key;

public class RouletteSnakeAction extends ScheduledAction {

    private final Key snakeKey;
    private final VCRobotAdapter robot;
    private static final String ACTION_ID = "roulette-snake";

    /**
     * 轮盘吃零食宏，执行时会协助快速按下零食键，从而尽可能提升恢复血量的速度
     * @param interval 吃零食间隔
     * @param snakeKey 吃零食按键
     */
    public RouletteSnakeAction(long interval, Key snakeKey) {
        super(ACTION_ID, interval);
        this.snakeKey = snakeKey;
        this.robot = RobotFactory.getRobot(ACTION_ID);
    }

    @Override
    public void schedule(MacroEvent event) {
        try {
            robot.simulate(this.snakeKey);
        } catch (InterruptedException ignored) {
        }
    }

}

