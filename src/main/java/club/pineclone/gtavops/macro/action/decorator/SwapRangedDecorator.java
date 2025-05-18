package club.pineclone.gtavops.macro.action.decorator;

import club.pineclone.gtavops.macro.Macro;
import club.pineclone.gtavops.macro.SimpleMacro;
import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.action.ActionEvent;
import club.pineclone.gtavops.macro.action.impl.BlockAction;
import club.pineclone.gtavops.macro.action.robot.RobotFactory;
import club.pineclone.gtavops.macro.action.ScheduledAction;
import club.pineclone.gtavops.macro.action.ScheduledActionDecorator;
import club.pineclone.gtavops.macro.action.robot.VCRobotAdapter;
import club.pineclone.gtavops.macro.trigger.*;
import io.vproxy.vfx.entity.input.Key;
import javafx.scene.input.MouseButton;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class SwapRangedDecorator extends ScheduledActionDecorator {

    private final Key leftButtonKey = new Key(MouseButton.PRIMARY);

    private final VCRobotAdapter robot;  /* 机器人 */
    private final Key defaultRangedWeaponKey;  /* 默认远程武器 */
    private final boolean swapDefaultRangedWeaponOnEmpty;  /* 当目标远程武器为空值时切换到默认 */

    private AtomicReference<Key> targetRangedWeaponKey = new AtomicReference<>();
    private final Map<Key, Key> sourceToTargetMap;  /* 监听源到目标的映射 */

    private Macro recorderMacro;  /* 记录器宏 */
    private Macro blockerMacro;  /* 屏蔽器宏 */
    private BlockAction blockAction;

    private final AtomicBoolean blockerMacroRunning = new AtomicBoolean(false);
    private final AtomicBoolean recorderMacroRunning = new AtomicBoolean(false);

    public static SwapRangedDecoratorBuilder builder() {
        return new SwapRangedDecoratorBuilder();
    }

    private SwapRangedDecorator(ScheduledAction delegate,
                               Key defaultRangedWeaponKey,
                               boolean swapDefaultRangedWeaponOnEmpty,
                               Map<Key, Key> sourceToTargetMap,
                               boolean enableBlockKey,
                               Key blockKey,
                               long blockDuration) {
        super(delegate);
        this.robot = RobotFactory.getRobot(delegate.getActionId());

        this.defaultRangedWeaponKey = defaultRangedWeaponKey;
        this.swapDefaultRangedWeaponOnEmpty = swapDefaultRangedWeaponOnEmpty;

        this.sourceToTargetMap = sourceToTargetMap;  /* 映射表 */
//        this.blockDuration = blockDuration;

        if (!this.sourceToTargetMap.isEmpty()) {
            /* 映射表不为空，创建子宏 */
            Trigger trigger = TriggerFactory.simple(new TriggerIdentity(TriggerMode.CLICK, sourceToTargetMap.keySet()));
            Action action = new RecordAction();
            recorderMacro = new SimpleMacro(trigger, action);
        }

        if (enableBlockKey) {
            /* 启用屏蔽 */
            Trigger trigger = TriggerFactory.simple(new TriggerIdentity(TriggerMode.HOLD, blockKey));
            //    private final long blockDuration;
            blockAction = new BlockAction(blockerMacroRunning, blockDuration);
            blockerMacro = new SimpleMacro(trigger, blockAction);
        }
    }

    @Override
    public void install() {
        delegate.install();
        if (recorderMacro != null) recorderMacro.install();
        if (blockerMacro != null) blockerMacro.install();
    }

    @Override
    public void uninstall() {
        if (recorderMacro != null) recorderMacro.uninstall();
        if (blockerMacro != null) blockerMacro.uninstall();
        delegate.uninstall();
    }

    @Override
    public boolean beforeActivate(ActionEvent event) throws Exception {
        if (!delegate.beforeActivate(event)) return false;
        if (recorderMacro != null) recorderMacroRunning.set(true);
        if (blockerMacro != null) blockerMacroRunning.set(true);
        return true;
    }

    /* 在宏(例如切枪偷速、近战偷速)执行结束之后，尝试切换远程武器 */
    @Override
    public void afterDeactivate(ActionEvent event) throws Exception {
        if (recorderMacro != null) recorderMacroRunning.set(false);
        if (blockerMacro != null) {
            blockerMacroRunning.set(false);
            if (blockAction.isBlocked()) return;
        }

        Key keyToUse = targetRangedWeaponKey.getAndSet(null);
        if (swapDefaultRangedWeaponOnEmpty && keyToUse == null) {
            keyToUse = defaultRangedWeaponKey;
        }

        if (keyToUse != null) {
            Thread.sleep(20);
            robot.simulate(keyToUse);  /* 切换到枪 */
            Thread.sleep(20);
            robot.simulate(leftButtonKey);  /* 点左键选择 */
        }

        delegate.afterDeactivate(event);
    }

    private class RecordAction extends Action {
        private final static String ACTION_ID = "action-ext";

        public RecordAction() {
            super(ACTION_ID);
        }

        @Override
        public void activate(ActionEvent event) {
            /* 触发记录，覆写原始targetRangedWeaponKey */
            if (!recorderMacroRunning.get()) return;
            targetRangedWeaponKey.set(sourceToTargetMap.get(event.getTriggerEvent().getInputSourceEvent().getKey()));
        }

        @Override
        public void deactivate(ActionEvent event) {

        }
    }

    public static class SwapRangedDecoratorBuilder {
        private Key defaultRangedWeaponKey;  /* 默认远程武器 */
        private boolean swapDefaultRangedWeaponOnEmpty;  /* 当目标远程武器为空值时切换到默认 */
        private long blockDuration;
        private boolean enableBlockKey = false;
        private Map<Key, Key> sourceToTargetMap;  /* 监听源到目标的映射 */
        protected ScheduledAction delegate;
        private Key blockKey;

        private SwapRangedDecoratorBuilder() {}

        public SwapRangedDecoratorBuilder defaultRangedWeaponKey(Key key) {
            this.defaultRangedWeaponKey = key;
            return this;
        }

        public SwapRangedDecoratorBuilder sourceToTargetMap(Map<Key, Key> sourceToTargetMap) {
            this.sourceToTargetMap = sourceToTargetMap;
            return this;
        }

        public SwapRangedDecoratorBuilder blockDuration(long blockDuration) {
            this.blockDuration = blockDuration;
            return this;
        }

        public SwapRangedDecoratorBuilder enableBlockKey(boolean flag) {
            this.enableBlockKey = flag;
            return this;
        }

        public SwapRangedDecoratorBuilder swapDefaultRangedWeaponOnEmpty(boolean flag) {
            this.swapDefaultRangedWeaponOnEmpty = flag;
            return this;
        }

        public SwapRangedDecoratorBuilder delegate(ScheduledAction delegate) {
            this.delegate = delegate;
            return this;
        }

        public SwapRangedDecoratorBuilder blockKey(Key key) {
            this.blockKey = key;
            return this;
        }

        public SwapRangedDecorator build() {
            if (!enableBlockKey) blockDuration = 0;
            return new SwapRangedDecorator(
                    delegate,
                    defaultRangedWeaponKey,
                    swapDefaultRangedWeaponOnEmpty,
                    sourceToTargetMap,
                    enableBlockKey,
                    blockKey,
                    blockDuration
            );
        }
    }
}
