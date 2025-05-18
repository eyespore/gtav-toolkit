package club.pineclone.gtavops.macro.trigger;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class ConditionalTrigger extends Trigger implements TriggerListener {

    private final Trigger main;  /* 主触发器 */
    private final Trigger blocker;  /* 屏蔽触发器 */
    private ConditionalTriggerState state;  /* 当前状态 */

    private final Map<ConditionalTriggerPhase, ConditionalTriggerState> stateMap = new HashMap<>();

    /* 条件触发器，如果block被按下，那么main不会触发 */
    public ConditionalTrigger(Trigger main, Trigger blocker) {
        this.main = main;
        this.blocker = blocker;

        stateMap.put(ConditionalTriggerPhase.IDLE, new IdleState(this));
        stateMap.put(ConditionalTriggerPhase.MAIN_ONLY, new MainOnlyState(this));
        stateMap.put(ConditionalTriggerPhase.BLOCKER_ONLY, new BlockerOnlyState(this));
        stateMap.put(ConditionalTriggerPhase.BOTH_ACTIVE, new BothActiveState(this));

        main.addListener(this);
        blocker.addListener(this);
    }

    @Override
    public void install() {
        main.install();
        blocker.install();
    }

    @Override
    public void uninstall() {
        main.uninstall();
        blocker.uninstall();
    }

    public void setState(ConditionalTriggerPhase phase) {
        this.state = stateMap.get(phase);
    }

    @Override
    public void onTriggerActivate(TriggerEvent event) {
        if (event.getSource() == main) {
            state.onMainActivate(event);
        } else if (event.getSource() == blocker) {
            state.onBlockerActivate(event);
        }
    }

    @Override
    public void onTriggerDeactivate(TriggerEvent event) {
        if (event.getSource() == main) {  /* 主键被松开，动作直接停止 */
            state.onMainDeactivate(event);
        } else if (event.getSource() == blocker) {
            state.onBlockerDeactivate(event);
        }
    }

    public enum ConditionalTriggerPhase {
        IDLE,
        MAIN_ONLY,
        BLOCKER_ONLY,
        BOTH_ACTIVE
    }

    /* 条件触发器状态 */
    private interface ConditionalTriggerState {
        default void onMainActivate(TriggerEvent event) {}
        default void onMainDeactivate(TriggerEvent event) {}
        default void onBlockerActivate(TriggerEvent event) {}
        default void onBlockerDeactivate(TriggerEvent event) {}
    }

    private static abstract class AbstractConditionalTriggerState implements ConditionalTriggerState {
        protected final ConditionalTrigger context;

        protected AbstractConditionalTriggerState(ConditionalTrigger context) {
            this.context = context;
        }

        protected void changeState(ConditionalTriggerPhase phase) {
            context.setState(phase);
        }
    }

    /* 无任何触发器触发时的状态 */
    private static class IdleState extends AbstractConditionalTriggerState {
        protected IdleState(ConditionalTrigger context) {
            super(context);
        }

        @Override
        public void onMainActivate(TriggerEvent event) {
            TriggerEvent wrappedEvent = TriggerEvent.ofNormal(context, event.getInputSourceEvent());
            context.activate(wrappedEvent);
            changeState(ConditionalTriggerPhase.MAIN_ONLY);
        }

        @Override
        public void onBlockerActivate(TriggerEvent event) {
            changeState(ConditionalTriggerPhase.BLOCKER_ONLY);
        }
    }

    /* 仅main被触发时的状态 */
    private static class MainOnlyState extends AbstractConditionalTriggerState {
        protected MainOnlyState(ConditionalTrigger context) {
            super(context);
        }

        @Override
        public void onMainDeactivate(TriggerEvent event) {
            /* main停止，发送一般停止信号 */
            TriggerEvent wrappedEvent = TriggerEvent.ofNormal(context, event.getInputSourceEvent());
            context.deactivate(wrappedEvent);
            changeState(ConditionalTriggerPhase.IDLE);
        }

        @Override
        public void onBlockerActivate(TriggerEvent event) {
            /* blocker被触发，发送带有blocker的停止信号 */
            TriggerEvent wrappedEvent = TriggerEvent.ofBlocked(context, event.getInputSourceEvent());
            context.deactivate(wrappedEvent);  /* 主键触发，组织主键 */
            changeState(ConditionalTriggerPhase.BOTH_ACTIVE);
        }
    }

    /* 仅blocker被触发时的状态 */
    private static class BlockerOnlyState extends AbstractConditionalTriggerState {
        protected BlockerOnlyState(ConditionalTrigger context) {
            super(context);
        }

        @Override
        public void onMainActivate(TriggerEvent event) {
            /* main触发，由于此时blocker被触发，不做任何操作 */
            changeState(ConditionalTriggerPhase.BOTH_ACTIVE);
        }

        @Override
        public void onBlockerDeactivate(TriggerEvent event) {
            /* blocker被终止，回到初始状态 */
            changeState(ConditionalTriggerPhase.IDLE);
        }
    }

    /* main和blocker同时触发时的状态 */
    private static class BothActiveState extends AbstractConditionalTriggerState {
        protected BothActiveState(ConditionalTrigger context) {
            super(context);
        }

        @Override
        public void onBlockerDeactivate(TriggerEvent event) {
            /* blocker重新停止，此时main正在执行，重新恢复动作，发送一般启动信号 */
            TriggerEvent wrappedEvent = TriggerEvent.ofNormal(context, event.getInputSourceEvent());
            context.deactivate(wrappedEvent);
            changeState(ConditionalTriggerPhase.MAIN_ONLY);
        }

        @Override
        public void onMainDeactivate(TriggerEvent event) {
            /* main停止，回归仅blocker状态 */
            changeState(ConditionalTriggerPhase.BLOCKER_ONLY);
        }
    }

}
