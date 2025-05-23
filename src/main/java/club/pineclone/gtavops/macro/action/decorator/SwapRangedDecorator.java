package club.pineclone.gtavops.macro.action.decorator;

import club.pineclone.gtavops.macro.Macro;
import club.pineclone.gtavops.macro.MacroContextHolder;
import club.pineclone.gtavops.macro.MacroRegistry;
import club.pineclone.gtavops.macro.SimpleMacro;
import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.action.ActionEvent;
import club.pineclone.gtavops.macro.action.robot.RobotFactory;
import club.pineclone.gtavops.macro.action.ScheduledAction;
import club.pineclone.gtavops.macro.action.ScheduledActionDecorator;
import club.pineclone.gtavops.macro.action.robot.VCRobotAdapter;
import club.pineclone.gtavops.macro.trigger.*;
import io.vproxy.vfx.entity.input.Key;
import javafx.scene.input.MouseButton;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class SwapRangedDecorator
        extends ScheduledActionDecorator
        implements MacroContextHolder {

    private final Key leftButtonKey = new Key(MouseButton.PRIMARY);

    private final VCRobotAdapter robot;  /* 机器人 */
    private final Key defaultRangedWeaponKey;  /* 默认远程武器 */
    private final boolean swapDefaultRangedWeaponOnEmpty;  /* 当目标远程武器为空值时切换到默认 */

    private final AtomicReference<Key> targetRangedWeaponKey = new AtomicReference<>();
    private final Map<Key, Key> sourceToTargetMap;  /* 监听源到目标的映射 */

    private UUID recorderMacroId;
    private final AtomicBoolean recorderRunning = new AtomicBoolean(false);

    public static SwapRangedDecoratorBuilder builder() {
        return new SwapRangedDecoratorBuilder();
    }

    private SwapRangedDecorator(ScheduledAction delegate,
                               Key defaultRangedWeaponKey,
                               boolean swapDefaultRangedWeaponOnEmpty,
                               Map<Key, Key> sourceToTargetMap,
                               boolean enableClearKey,
                               Key clearKey) {
        super(delegate);
        this.robot = RobotFactory.getRobot(delegate.getActionId());

        this.defaultRangedWeaponKey = defaultRangedWeaponKey;
        this.swapDefaultRangedWeaponOnEmpty = swapDefaultRangedWeaponOnEmpty;

        this.sourceToTargetMap = sourceToTargetMap;  /* 映射表 */

        if (enableClearKey) this.sourceToTargetMap.put(clearKey, null);

        if (!this.sourceToTargetMap.isEmpty()) {
            /* 映射表不为空，创建子宏 */
            Trigger trigger = TriggerFactory.simple(new TriggerIdentity(TriggerMode.CLICK, sourceToTargetMap.keySet()));
            Action action = new RecordAction();
            recorderMacroId = MACRO_FACTORY.createSimpleMacro(trigger, action);
        }
    }

    @Override
    public void install() {
        delegate.install();
        MACRO_REGISTRY.install(recorderMacroId);
    }

    @Override
    public void uninstall() {
        MACRO_REGISTRY.uninstall(recorderMacroId);
        delegate.uninstall();
    }

    @Override
    public boolean beforeActivate(ActionEvent event) throws Exception {
        if (!delegate.beforeActivate(event)) return false;
        recorderRunning.set(true);

        if (swapDefaultRangedWeaponOnEmpty) {
            targetRangedWeaponKey.set(defaultRangedWeaponKey);
        }

        return true;
    }

    /* 在宏(例如切枪偷速、近战偷速)执行结束之后，尝试切换远程武器 */
    @Override
    public void afterDeactivate(ActionEvent event) throws Exception {
        recorderRunning.set(false);

        Key keyToUse = targetRangedWeaponKey.getAndSet(null);
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
            if (!recorderRunning.get()) return;
            targetRangedWeaponKey.set(sourceToTargetMap.get(event.getTriggerEvent().getInputSourceEvent().getKey()));
        }

        @Override
        public void deactivate(ActionEvent event) {

        }
    }

    public static class SwapRangedDecoratorBuilder {
        private Key defaultRangedWeaponKey;  /* 默认远程武器 */
        private boolean swapDefaultRangedWeaponOnEmpty;  /* 当目标远程武器为空值时切换到默认 */
        private boolean enableBlockKey = false;
        private Map<Key, Key> sourceToTargetMap;  /* 监听源到目标的映射 */
        protected ScheduledAction delegate;
        private Key clearKey;

        private SwapRangedDecoratorBuilder() {}

        public SwapRangedDecoratorBuilder defaultRangedWeaponKey(Key key) {
            this.defaultRangedWeaponKey = key;
            return this;
        }

        public SwapRangedDecoratorBuilder sourceToTargetMap(Map<Key, Key> sourceToTargetMap) {
            this.sourceToTargetMap = sourceToTargetMap;
            return this;
        }

        public SwapRangedDecoratorBuilder enableClearKey(boolean flag) {
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

        public SwapRangedDecoratorBuilder clearKey(Key key) {
            this.clearKey = key;
            return this;
        }

        public SwapRangedDecorator build() {
            return new SwapRangedDecorator(
                    delegate,
                    defaultRangedWeaponKey,
                    swapDefaultRangedWeaponOnEmpty,
                    sourceToTargetMap,
                    enableBlockKey,
                    clearKey
            );
        }
    }
}
