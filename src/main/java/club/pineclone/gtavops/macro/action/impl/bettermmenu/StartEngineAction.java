package club.pineclone.gtavops.macro.action.impl.bettermmenu;

import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.action.ActionEvent;
import club.pineclone.gtavops.macro.action.robot.RobotFactory;
import club.pineclone.gtavops.macro.action.robot.VCRobotAdapter;
import club.pineclone.gtavops.macro.trigger.TriggerStatus;
import io.vproxy.base.util.Logger;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.entity.input.KeyCode;
import io.vproxy.vfx.entity.input.MouseWheelScroll;

/* 快速点火 */
public class StartEngineAction extends Action {

    private final long mouseScrollInterval;
    private final long enterKeyInterval;
    private final long timeUtilMMenuLoaded;
    private final boolean enableDoubleClickToOpenDoor;

    private final VCRobotAdapter robot;

    public static final String ACTION_ID = "better-m-menu";

    private final Key mouseScrollDown = new Key(new MouseWheelScroll(MouseWheelScroll.Direction.DOWN, 1));
    private final Key mouseScrollUp = new Key(new MouseWheelScroll(MouseWheelScroll.Direction.UP, 1));
    private final Key enterKey = new Key(KeyCode.ENTER);
    private final Key menuKey;

    public StartEngineAction(Key menukey,
                             long mouseScrollInterval,
                             long enterKeyInterval,
                             long timeUtilMMenuLoaded,
                             boolean enableDoubleClickToOpenDoor) {
        super(ACTION_ID);
        this.mouseScrollInterval = mouseScrollInterval;
        this.enterKeyInterval = enterKeyInterval;
        this.timeUtilMMenuLoaded = timeUtilMMenuLoaded;
        this.enableDoubleClickToOpenDoor = enableDoubleClickToOpenDoor;
        this.robot = RobotFactory.getRobot();
        this.menuKey = menukey;
    }

    @Override
    public void activate(ActionEvent event) throws Exception {
//        Logger.lowLevelDebug(event.toString());
        boolean shouldOpenVehicleDoor = false;

        if (enableDoubleClickToOpenDoor) {
            /* 启用双击开门 */
            if (event.getTriggerEvent().getTriggerStatus().equals(TriggerStatus.DOUBLE_CLICK)) {
                /* 双击事件，开启车门 */
                shouldOpenVehicleDoor = true;
            }
        }

//        Logger.lowLevelDebug("should open door: " + shouldOpenVehicleDoor);
        pressM();
        Thread.sleep(timeUtilMMenuLoaded);  /* 解决 M 键菜单出现过晚的问题 */

        mouseScrollDown();
        mouseScrollDown();
        pressEnter();
        mouseScrollUp();
        pressEnter();
        mouseScrollDown();
        mouseScrollDown();
        if (shouldOpenVehicleDoor) pressEnter();
        for (int i = 0; i < 4; i++) mouseScrollDown();
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
