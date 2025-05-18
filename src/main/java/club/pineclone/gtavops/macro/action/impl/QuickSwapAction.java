package club.pineclone.gtavops.macro.action.impl;

import club.pineclone.gtavops.macro.Macro;
import club.pineclone.gtavops.macro.MacroPriorityContext;
import club.pineclone.gtavops.macro.SimpleMacro;
import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.action.ActionEvent;
import club.pineclone.gtavops.macro.action.robot.RobotFactory;
import club.pineclone.gtavops.macro.action.robot.VCRobotAdapter;

import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerFactory;
import club.pineclone.gtavops.macro.trigger.TriggerIdentity;
import club.pineclone.gtavops.macro.trigger.TriggerMode;
import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import io.vproxy.vfx.entity.input.Key;
import javafx.scene.input.MouseButton;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;


/* 快速切枪 */
public class QuickSwapAction extends Action {

    private final VCRobotAdapter robot;
    private final Map<Key, Key> sourceToTargetMap = new HashMap<>();

    protected static final String ACTION_ID = "quick-swap";

    private final Key leftButtonKey = new Key(MouseButton.PRIMARY);

    private Macro blockerMacro;  /* 屏蔽器宏 */
    private BlockAction blockAction;

    public QuickSwapAction(Map<Key, Key> sourceToTargetMap, boolean enableBlockKey, Key blockKey ,long blockDuration) {
        super(ACTION_ID);
        this.sourceToTargetMap.putAll(sourceToTargetMap);
        this.robot = RobotFactory.getRobot(ACTION_ID);

        if (enableBlockKey) {
            /* 启用屏蔽 */
            Trigger trigger = TriggerFactory.simple(new TriggerIdentity(TriggerMode.HOLD, blockKey));
            blockAction = new BlockAction(blockDuration);
            blockerMacro = new SimpleMacro(trigger, blockAction);
        }
    }

    @Override
    public void install() {
        super.install();
        if (blockerMacro != null) blockerMacro.install();
    }

    @Override
    public void uninstall() {
        if (blockerMacro != null) blockerMacro.uninstall();
        super.uninstall();
    }

    @Override
    public void activate(ActionEvent event) {
        if (blockerMacro != null && blockAction.isBlocked()) return;

        try {
            Thread.sleep(20);
            Key key = event.getTriggerEvent().getInputSourceEvent().getKey();
            robot.simulate(sourceToTargetMap.get(key));  /* 切换到枪 */
            Thread.sleep(20);
            robot.simulate(leftButtonKey);  /* 点左键选择 */
        } catch (Exception e) {
            Logger.error(LogType.SYS_ERROR, e.getMessage());
        }
    }

    @Override
    public void deactivate(ActionEvent event) {

    }
}
