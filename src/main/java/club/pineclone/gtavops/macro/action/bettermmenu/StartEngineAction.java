package club.pineclone.gtavops.macro.action.bettermmenu;

import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.action.ActionEvent;
import club.pineclone.gtavops.macro.action.robot.RobotFactory;
import club.pineclone.gtavops.macro.action.robot.VCRobotAdapter;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.entity.input.KeyCode;

/* 快速点火 */
public class StartEngineAction extends Action {

    private boolean running = false;
    private final long arrowKeyInterval;
    private final long enterKeyInterval;

    private final VCRobotAdapter robot;

    private final Key downKey = new Key(KeyCode.DOWN);
    private final Key upKey = new Key(KeyCode.UP);
    private final Key enterKey = new Key(KeyCode.ENTER);
    private final Key menuKey;

    public StartEngineAction(Key menukey, long arrowKeyInterval, long enterKeyInterval) {
        this.arrowKeyInterval = arrowKeyInterval;
        this.enterKeyInterval = enterKeyInterval;
        this.robot = RobotFactory.getRobot(menukey);
        this.menuKey = menukey;
    }

    @Override
    public void activate(ActionEvent event) {
        if (running) return;
        running = true;
        System.out.println("execute!");
        try {
            pressM();
            pressDown();
            pressDown();
            pressEnter();
            pressUp();
            pressEnter();
            pressUp();
            pressUp();
            pressUp();
            pressEnter();
            pressEnter();
            pressM();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            running = false;
        }
    }

    private void pressM() throws Exception {
        robot.simulate(menuKey);
        awaitArrow();
    }

    private void pressDown() throws Exception {
        robot.simulate(downKey);
        awaitArrow();
    }

    private void pressEnter() throws Exception {
        robot.simulate(enterKey);
        awaitEnter();
    }

    private void pressUp() throws Exception {
        robot.simulate(upKey);
        awaitArrow();
    }

    private void awaitArrow() throws InterruptedException {
        Thread.sleep(arrowKeyInterval);
    }

    private void awaitEnter() throws InterruptedException {
        Thread.sleep(enterKeyInterval);
    }

    @Override
    public void deactivate(ActionEvent event) {

    }
}
