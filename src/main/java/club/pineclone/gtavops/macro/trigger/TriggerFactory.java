package club.pineclone.gtavops.macro.trigger;

import club.pineclone.gtavops.macro.trigger.policy.ActivationPolicy;
import club.pineclone.gtavops.macro.trigger.source.KeyboardSource;
import club.pineclone.gtavops.macro.trigger.source.MouseButtonSource;
import club.pineclone.gtavops.macro.trigger.source.MouseScrollSource;
import io.vproxy.vfx.entity.input.Key;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TriggerFactory {

    /** 用来存放已创建的 CompositeTrigger，键为 id */
    private static final ConcurrentMap<TriggerIdentity, Trigger> singleTriggerRegistry = new ConcurrentHashMap<>();
    private static final ConcurrentMap<TriggerIdentityGroup, Trigger> multipleTriggerRegistry = new ConcurrentHashMap<>();

    private TriggerFactory() {}

    public static Trigger simple(TriggerIdentity identity) {
        return singleTriggerRegistry.computeIfAbsent(identity, key -> createSimpleTrigger(identity));
    }

    public static Trigger composite(TriggerIdentity... identities) {
        Set<TriggerIdentity> set = new HashSet<>(Arrays.asList(identities));  /* 去重 */
        if (set.size() == 1) {
            /* 简单触发器 */
            TriggerIdentity identity = set.iterator().next();  // 如果已经存在则直接返回，否则创建并注册
            return simple(identity);
        } else {
            /* 组合触发器(未来可能更多拓展) */
            TriggerIdentityGroup identityGroup = new TriggerIdentityGroup(set);
            return multipleTriggerRegistry.computeIfAbsent(identityGroup, group -> {
                List<Trigger> triggers = set.stream().map(TriggerFactory::simple).toList();
                return new CompositeTrigger(triggers);
            });
        }
    }

    @Deprecated
    public static Trigger condition(TriggerIdentity main, TriggerIdentity condition) {
        Trigger mainTrigger = simple(main);
        return new ConditionalTrigger(mainTrigger, simple(condition));
    }

    @Deprecated
    public static Trigger condition(Trigger main, TriggerIdentity condition) {
        Trigger conditionTrigger = simple(condition);
        return new ConditionalTrigger(main, conditionTrigger);
    }

    @Deprecated
    public static Trigger condition(Trigger main, Trigger condition) {
        return new ConditionalTrigger(main, condition);
    }

    /* 构建最小的触发器单位 */
    private static Trigger createSimpleTrigger(TriggerIdentity identity) {
        Set<Key> key = identity.getKeys();
        TriggerType type = identity.getType();
        TriggerMode mode = identity.getMode();

        switch (type) {
            case KEYBOARD -> {
                return switch (mode) {
                    case HOLD   -> new SimpleTrigger(new KeyboardSource(key), ActivationPolicy.hold());
                    case TOGGLE -> new SimpleTrigger(new KeyboardSource(key), ActivationPolicy.toggle());
                    case CLICK -> new SimpleTrigger(new KeyboardSource(key), ActivationPolicy.click());
                };
            }
            case MOUSE_BUTTON -> {
                return switch (mode) {
                    case HOLD   -> new SimpleTrigger(new MouseButtonSource(key), ActivationPolicy.hold());
                    case TOGGLE -> new SimpleTrigger(new MouseButtonSource(key), ActivationPolicy.toggle());
                    case CLICK -> new SimpleTrigger(new MouseButtonSource(key), ActivationPolicy.click());
                };
            }
            case SCROLL_WHEEL -> {
                // 已在 TriggerIdentity 中保证只能与 TOGGLE 组合
                return switch (mode) {
                    case TOGGLE -> new SimpleTrigger(new MouseScrollSource(key), ActivationPolicy.toggle());
                    case CLICK -> new SimpleTrigger(new MouseScrollSource(key), ActivationPolicy.click());
                    default -> throw new IllegalStateException("Unexpected value: " + mode);
                };
            }
            default -> throw new IllegalArgumentException("Unsupported TriggerType: " + type);
        }
    }
}
