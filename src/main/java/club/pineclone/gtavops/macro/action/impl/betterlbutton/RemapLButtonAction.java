package club.pineclone.gtavops.macro.action.impl.betterlbutton;

import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.MacroEvent;
import club.pineclone.gtavops.macro.action.robot.RobotFactory;
import club.pineclone.gtavops.macro.action.robot.VCRobotAdapter;
import io.vproxy.vfx.entity.input.Key;
import javafx.scene.input.MouseButton;

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
