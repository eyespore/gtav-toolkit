package club.pineclone.gtavops.macro.action.impl.betterpmenu;

import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.action.ActionEvent;
import club.pineclone.gtavops.macro.action.robot.RobotFactory;
import club.pineclone.gtavops.macro.action.robot.VCRobotAdapter;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.entity.input.KeyCode;
import io.vproxy.vfx.entity.input.MouseWheelScroll;

/* 快速点火 */
public class JoinABookmarkedJobAction extends Action {

    private final long mouseScrollInterval;
    private final long enterKeyInterval;
    private final long timeUtilPMenuLoaded;
    private final long timeUtilJobsLoaded;

    private final VCRobotAdapter robot;

    public static final String ACTION_ID = "better-p-menu";

    private final Key enterKey = new Key(KeyCode.ENTER);
    private final Key rightKey = new Key(KeyCode.RIGHT);

    private final Key mouseScrollUpKey = new Key(new MouseWheelScroll(MouseWheelScroll.Direction.UP, 1));
    private final Key mouseScrollDownKey = new Key(new MouseWheelScroll(MouseWheelScroll.Direction.DOWN, 1));

    private final Key menuKey = new Key(KeyCode.P);  /* P 菜单按键 */

    public JoinABookmarkedJobAction(
            long mouseScrollInterval,
            long enterKeyInterval,
            long timeUtilPMenuLoaded,
            long timeUtilJobsLoaded
    ) {
        super(ACTION_ID);

        this.mouseScrollInterval = mouseScrollInterval;
        this.enterKeyInterval = enterKeyInterval;
        this.timeUtilPMenuLoaded = timeUtilPMenuLoaded;
        this.timeUtilJobsLoaded = timeUtilJobsLoaded;
        this.robot = RobotFactory.getRobot();
    }

    @Override
    public void activate(ActionEvent event) throws Exception {
        pressP();
        Thread.sleep(200);
        pressRight();
        awaitTimeUtilPMenuLoaded();  /* 等待列表加载 */
        pressEnter();  /* 选择列表 */
        Thread.sleep(200);
        pressEnter();  /* 差事 */
        mouseScrollDown();  /* 进行差事 */
        Thread.sleep(200);
        pressEnter();
        Thread.sleep(200);
        mouseScrollDown();  /* 已收藏的 */
        Thread.sleep(200);
        pressEnter();

        Thread.sleep(timeUtilJobsLoaded);  /* 等待差事加载 */

        for (int i = 0; i < 5; i++) mouseScrollDown();  /* 夺取 */
        Thread.sleep(200);
        pressEnter();
        Thread.sleep(200);
        pressEnter();
        Thread.sleep(200);
        pressEnter();  /* 确认进行该差事 */
    }

    private void mouseScrollDown() throws Exception {
        robot.mouseWheel(mouseScrollDownKey);
        Thread.sleep(mouseScrollInterval);
    }

    private void mouseScrollUp() throws Exception {
        robot.mouseWheel(mouseScrollUpKey);
        Thread.sleep(mouseScrollInterval);
    }

    private void pressP() throws Exception {
        robot.simulate(menuKey);
        awaitArrow();
    }

    private void pressRight() throws Exception {
        robot.simulate(rightKey);
        awaitArrow();
    }

    private void pressEnter() throws Exception {
        robot.simulate(enterKey);
        awaitEnter();
    }

    private void awaitArrow() throws InterruptedException {
        Thread.sleep(mouseScrollInterval);
    }

    private void awaitEnter() throws InterruptedException {
        Thread.sleep(enterKeyInterval);
    }

    private void awaitTimeUtilPMenuLoaded() throws InterruptedException {
        Thread.sleep(timeUtilPMenuLoaded);
    }
}
