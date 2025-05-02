package club.pineclone.gtavops.macro.swapglitch;

import club.pineclone.gtavops.macro.action.*;
import io.vproxy.vfx.entity.input.Key;

public class SwapGlitchAction extends ScheduledAction {

    private final VCRobotAdapter robot;
    private final Key hotkey;

    /**
     * 切枪偷速宏
     * @param hotkey 武器轮盘键
     * @param interval 切枪间隔，经过实测50ms最佳
     */
    public SwapGlitchAction(Key hotkey, long interval) {
        super(interval);
        this.hotkey = hotkey;
        robot = RobotFactory.getRobot(hotkey);
    }

    @Override
    public void schedule() throws Exception {
        robot.simulate(this.hotkey);
    }
}
