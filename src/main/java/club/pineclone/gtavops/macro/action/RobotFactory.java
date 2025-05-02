package club.pineclone.gtavops.macro.action;

import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import io.vproxy.vfx.entity.input.Key;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class RobotFactory {

    private static final Map<Integer, VCRobotAdapter> adapters = new HashMap<>();
    public static VCRobotAdapter getRobot(Key key) {
        if (key.key != null) {
            return doGetRobot(0);  /* 按键 */
        } else if (key.button != null) {
            return doGetRobot(1);  /* 鼠标 */
        } else if (key.scroll != null) {
            return doGetRobot(2);  /* 滚轮 */
        }
        return null;
    }

    /* 享元 */
    private static VCRobotAdapter doGetRobot(int identity) {
        return adapters.computeIfAbsent(identity, integer -> {
            try {
                Robot robot = new Robot();
                return switch (identity) {
                    case 0 -> new KeyRobotAdapter(robot);
                    case 1 -> new MouseRobotAdapter(robot);
                    case 2 -> new ScrollWheelRobotAdapter(robot);
                    default -> throw new IllegalStateException("Unexpected value: " + identity);
                };
            } catch (AWTException e) {
                Logger.error(LogType.SYS_ERROR, "cannot successfully create robot instance");
                throw new RuntimeException(e);
            }
        });
    }

    private static class KeyRobotAdapter extends VCRobotAdapter {
        public KeyRobotAdapter(Robot robot) {
            super(robot);
        }

        @Override
        public void simulate(Key key) throws Exception {
            this.keyPress(key);
            Thread.sleep(20);
            keyRelease(key);
        }
    }

    private static class MouseRobotAdapter extends VCRobotAdapter {
        public MouseRobotAdapter(Robot robot) {
            super(robot);
        }

        @Override
        public void simulate(Key key) throws Exception {
            mousePress(key);
            Thread.sleep(20);
            mouseRelease(key);
        }
    }

    private static class ScrollWheelRobotAdapter extends VCRobotAdapter {
        public ScrollWheelRobotAdapter(Robot robot) {
            super(robot);
        }

        @Override
        public void simulate(Key key) throws Exception {
            this.mouseWheel(key);
        }
    }

}
