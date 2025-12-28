package club.pineclone.gtavops.macro.action.impl.bettermmenu;

import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.action.ActionEvent;
import club.pineclone.gtavops.macro.action.robot.RobotFactory;
import club.pineclone.gtavops.macro.action.robot.VCRobotAdapter;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.entity.input.KeyCode;
import io.vproxy.vfx.entity.input.MouseWheelScroll;

/* 快速点火 */
public class StartEngineAction extends Action {

    private final long mouseScrollInterval;
    private final long enterKeyInterval;
    private final long timeUtilMMenuLoaded;

    private final VCRobotAdapter robot;

    public static final String ACTION_ID = "better-m-menu";

    private final Key downKey = new Key(KeyCode.DOWN);


    private final Key mouseScrollDown = new Key(new MouseWheelScroll(MouseWheelScroll.Direction.DOWN, 1));
    private final Key mouseScrollUp = new Key(new MouseWheelScroll(MouseWheelScroll.Direction.UP, 1));
    private final Key upKey = new Key(KeyCode.UP);
    private final Key enterKey = new Key(KeyCode.ENTER);
    private final Key menuKey;

    public StartEngineAction(Key menukey, long mouseScrollInterval,
                             long enterKeyInterval, long timeUtilMMenuLoaded) {
        super(ACTION_ID);
        this.mouseScrollInterval = mouseScrollInterval;
        this.enterKeyInterval = enterKeyInterval;
        this.timeUtilMMenuLoaded = timeUtilMMenuLoaded;
        this.robot = RobotFactory.getRobot();
        this.menuKey = menukey;
    }

    @Override
    public void activate(ActionEvent event) throws Exception {
        pressM();

        Thread.sleep(timeUtilMMenuLoaded);  /* 解决M键菜单出现过晚的问题 */

        mouseScrollDown();
        mouseScrollDown();
        pressEnter();
        mouseScrollUp();
        pressEnter();
        mouseScrollUp();
        mouseScrollUp();
        mouseScrollUp();
        pressEnter();
        pressEnter();
        pressM();
    }

    private void pressM() throws Exception {
        robot.simulate(menuKey);
        Thread.sleep(enterKeyInterval);
    }

    private void mouseScrollDown() throws Exception {
        robot.mouseWheel(mouseScrollDown);
        Thread.sleep(mouseScrollInterval);
    }

    private void mouseScrollUp() throws Exception {
        robot.mouseWheel(mouseScrollUp);
        Thread.sleep(mouseScrollInterval);
    }

    private void pressEnter() throws Exception {
        robot.simulate(enterKey);
        Thread.sleep(enterKeyInterval);
    }
}
