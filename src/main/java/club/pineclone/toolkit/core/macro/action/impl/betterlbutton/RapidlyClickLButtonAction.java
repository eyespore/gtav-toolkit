package club.pineclone.toolkit.core.macro.action.impl.betterlbutton;

import club.pineclone.toolkit.core.macro.MacroEvent;
import club.pineclone.toolkit.core.macro.action.ScheduledAction;
import club.pineclone.toolkit.core.macro.action.robot.RobotFactory;
import club.pineclone.toolkit.core.macro.action.robot.VCRobotAdapter;
import club.pineclone.toolkit.core.macro.input.Key;
import club.pineclone.toolkit.core.macro.input.MouseButton;

public class RapidlyClickLButtonAction extends ScheduledAction {

    private static final String ACTION_ID = "better-l-button";
    private static final Key leftButton = new Key(MouseButton.PRIMARY);
    private final VCRobotAdapter robot;

    public RapidlyClickLButtonAction(long interval) {
        super(ACTION_ID, interval);
        robot = RobotFactory.getRobot(ACTION_ID);
    }

    @Override
    public void schedule(MacroEvent event) {
        try {
            robot.simulate(leftButton);
        } catch (InterruptedException ignored) {
        }
    }
}
