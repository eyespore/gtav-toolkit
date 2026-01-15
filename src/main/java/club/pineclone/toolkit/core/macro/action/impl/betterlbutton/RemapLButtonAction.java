package club.pineclone.toolkit.core.macro.action.impl.betterlbutton;

import club.pineclone.toolkit.core.macro.action.Action;
import club.pineclone.toolkit.core.macro.MacroEvent;
import club.pineclone.toolkit.core.macro.action.robot.RobotFactory;
import club.pineclone.toolkit.core.macro.action.robot.VCRobotAdapter;
import club.pineclone.toolkit.core.macro.input.Key;
import club.pineclone.toolkit.core.macro.input.MouseButton;

public class RemapLButtonAction extends Action {

    private static final String ACTION_ID = "better-l-button";
    private static final Key leftButton = new Key(MouseButton.PRIMARY);
    private final VCRobotAdapter robot;

    public RemapLButtonAction() {
        super(ACTION_ID);
        robot = RobotFactory.getRobot(ACTION_ID);
    }

    @Override
    public void activate(MacroEvent event) {
        robot.mousePress(leftButton);
    }

    @Override
    public void deactivate(MacroEvent event) {
        robot.mouseRelease(leftButton);
    }
}
