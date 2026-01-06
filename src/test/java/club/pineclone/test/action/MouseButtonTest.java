package club.pineclone.test.action;

import club.pineclone.gtavops.macro.MacroEvent;
import club.pineclone.gtavops.macro.action.ScheduledAction;
import club.pineclone.gtavops.macro.action.robot.RobotAdapter;
import club.pineclone.gtavops.macro.action.robot.RobotFactory;
import io.vproxy.vfx.entity.input.Key;
import javafx.scene.input.MouseButton;
import junit.framework.TestCase;

public class MouseButtonTest extends TestCase {

    public void testClick() throws InterruptedException {
        ScheduledAction action = new ScheduledAction("test", 1000) {
            final RobotAdapter robot;

            {
                robot = RobotFactory.getRobot(getActionId());
            }

            @Override
            public void schedule(MacroEvent event) {
                try {
                    robot.simulate(new Key(MouseButton.SECONDARY));
                } catch (InterruptedException ignored) {
                }
            }
        };

        action.activate(null);

        Thread.sleep(5000);
    }

}
