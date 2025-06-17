package club.pineclone.gtavops.macro.action.impl;

import club.pineclone.gtavops.macro.MacroPriorityContext;
import club.pineclone.gtavops.macro.action.ActionEvent;
import club.pineclone.gtavops.macro.action.ScheduledAction;
import club.pineclone.gtavops.macro.action.robot.RobotFactory;
import club.pineclone.gtavops.macro.action.robot.VCRobotAdapter;
import io.vproxy.vfx.entity.input.Key;

public class MeleeGlitchAction extends ScheduledAction {

    private final Key meleeSnakeScrollKey;  /* 近战武器零食滚轮键 */
    private final VCRobotAdapter robot;  /* 执行机器人 */

    MacroPriorityContext context = MacroPriorityContext.getInstance();
    public static final String ACTION_ID = "melee-glitch";

    @Override
    public boolean beforeActivate(ActionEvent event) {
        context.blockSwapGlitch.set(true);  /* 动作开始之前，阻塞切枪偷速 */
        return true;
    }

    @Override
    public void afterDeactivate(ActionEvent event) {
        context.blockSwapGlitch.set(false);  /* 动作完全撤销之后，允许切枪偷速 */
    }

    /* 近战偷速宏 */
    public MeleeGlitchAction(long interval, Key meleeSnakeScrollKey) {
        super(ACTION_ID, interval);
        this.meleeSnakeScrollKey = meleeSnakeScrollKey;
        this.robot = RobotFactory.getRobot(ACTION_ID);
    }

    @Override
    public void schedule(ActionEvent event) throws Exception {
        robot.simulate(meleeSnakeScrollKey);
    }
}
