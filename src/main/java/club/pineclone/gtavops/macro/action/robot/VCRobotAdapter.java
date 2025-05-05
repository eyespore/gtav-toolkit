package club.pineclone.gtavops.macro.action.robot;

import club.pineclone.gtavops.utils.KeyUtils;
import io.vproxy.vfx.entity.input.Key;

import java.awt.*;

public class VCRobotAdapter implements RobotAdapter {

    protected final Robot robot;

    public VCRobotAdapter(Robot robot) {
        this.robot = robot;
    }

    @Override
    public void simulate(Key key) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void simulate(Key key, long delay) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void keyPress(Key key) {
        int vkCode = KeyUtils.toVKCode(key.key);
        robot.keyPress(vkCode);
    }

    public final void keyRelease(Key key) {
        int vkCode = KeyUtils.toVKCode(key.key);
        robot.keyRelease(vkCode);
    }

    public final void mousePress(Key key) {
        int mask = KeyUtils.toVKMouse(key.button);
        robot.mousePress(mask);
    }

    public final void mouseRelease(Key key) {
        int mask = KeyUtils.toVKMouse(key.button);
        robot.mouseRelease(mask);
    }

    public final void mouseWheel(Key key) {
        int mask = KeyUtils.toVCScroll(key.scroll);
        robot.mouseWheel(mask);
    }

    @Override
    public final void mousePress(int button) {
        robot.mousePress(button);
    }

    @Override
    public final void mouseRelease(int button) {
        robot.mouseRelease(button);
    }

    @Override
    public final void mouseWheel(int wheelAmt) {
        robot.mouseWheel(wheelAmt);
    }

    @Override
    public final void keyPress(int keyCode) {
        robot.keyPress(keyCode);
    }

    @Override
    public final void keyRelease(int keyCode) {
        robot.keyRelease(keyCode);
    }
}
