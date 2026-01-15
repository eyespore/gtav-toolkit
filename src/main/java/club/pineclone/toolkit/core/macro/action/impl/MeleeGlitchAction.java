package club.pineclone.toolkit.core.macro.action.impl;

import club.pineclone.toolkit.core.macro.MacroPriorityContext;
import club.pineclone.toolkit.core.macro.MacroEvent;
import club.pineclone.toolkit.core.macro.action.ScheduledAction;
import club.pineclone.toolkit.core.macro.action.robot.RobotFactory;
import club.pineclone.toolkit.core.macro.action.robot.VCRobotAdapter;
import club.pineclone.toolkit.core.macro.input.Key;

public class MeleeGlitchAction extends ScheduledAction {

    private final Key meleeSnakeScrollKey;  /* 近战武器零食滚轮键 */
    private final VCRobotAdapter robot;  /* 执行机器人 */

    MacroPriorityContext context = MacroPriorityContext.getInstance();
    public static final String ACTION_ID = "melee-glitch";

    @Override
    public boolean beforeActivate(MacroEvent event) {
        context.blockSwapGlitch.set(true);  /* 动作开始之前，阻塞切枪偷速 */
        return true;
    }

    @Override
    public void afterDeactivate(MacroEvent event) {
        context.blockSwapGlitch.set(false);  /* 动作完全撤销之后，允许切枪偷速 */
    }

    /* 近战偷速宏 */
    public MeleeGlitchAction(long interval, Key meleeSnakeScrollKey) {
        super(ACTION_ID, interval);
        this.meleeSnakeScrollKey = meleeSnakeScrollKey;
        this.robot = RobotFactory.getRobot(ACTION_ID);
    }

    @Override
    public void schedule(MacroEvent event) {
        try {
            robot.simulate(meleeSnakeScrollKey);
        } catch (InterruptedException ignored) {

        }
    }
}
