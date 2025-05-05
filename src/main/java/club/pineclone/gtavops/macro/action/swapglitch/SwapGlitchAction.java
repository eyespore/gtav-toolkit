package club.pineclone.gtavops.macro.action.swapglitch;

import club.pineclone.gtavops.macro.action.*;
import club.pineclone.gtavops.macro.action.robot.RobotFactory;
import club.pineclone.gtavops.macro.action.robot.VCRobotAdapter;
import io.vproxy.vfx.entity.input.Key;

public class SwapGlitchAction extends ScheduledAction {

    private final VCRobotAdapter robot;
    private final Key hotkey;

    /**
     * 切枪偷速宏，辅助重复点按武器轮盘键，实现切枪保持小哑巴的战斗状态，从而偷取额外的移动速度
     * 在速通中很有用
     * @param hotkey 武器轮盘键
     * @param interval 切枪间隔，经过实测50ms最佳
     */
    public SwapGlitchAction(Key hotkey, long interval) {
        super(interval);
        this.hotkey = hotkey;
        robot = RobotFactory.getRobot(hotkey);
    }

    @Override
    public void schedule(ActionEvent event) throws Exception {
        robot.simulate(this.hotkey);
    }
}
