package club.pineclone.gtavops.macro.trigger;

import club.pineclone.gtavops.macro.trigger.policy.ActivationPolicy;
import club.pineclone.gtavops.macro.trigger.source.KeySource;
import club.pineclone.gtavops.macro.trigger.source.MouseSource;
import club.pineclone.gtavops.macro.trigger.source.ScrollSource;
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

    public static Trigger getTrigger(TriggerIdentity... identities) {
        Set<TriggerIdentity> set = new HashSet<>(Arrays.asList(identities));  /* 去重 */

        if (set.size() == 1) {
            /* 简单触发器 */
            TriggerIdentity identity = set.iterator().next();  // 如果已经存在则直接返回，否则创建并注册
            return singleTriggerRegistry.computeIfAbsent(identity, key -> createSimpleTrigger(identity));
        } else {
            /* 组合触发器(未来可能更多拓展) */
            TriggerIdentityGroup identityGroup = new TriggerIdentityGroup(set);
            return multipleTriggerRegistry.computeIfAbsent(identityGroup, group -> {
                List<Trigger> triggers = set.stream().map(TriggerFactory::getTrigger).toList();
                return new CompositeTrigger(triggers);
            });
        }
    }

    /* 构建最小的触发器单位 */
    private static Trigger createSimpleTrigger(TriggerIdentity identity) {
        Key key = identity.getKey();
        TriggerType type = identity.getType();
        TriggerMode mode = identity.getMode();

        switch (type) {
            case KEYBOARD -> {
                return switch (mode) {
                    case HOLD   -> new SimpleTrigger(new KeySource(key), ActivationPolicy.Hold());
                    case TOGGLE -> new SimpleTrigger(new KeySource(key), ActivationPolicy.Toggle());
                };
            }
            case MOUSE_BUTTON -> {
                return switch (mode) {
                    case HOLD   -> new SimpleTrigger(new MouseSource(key), ActivationPolicy.Hold());
                    case TOGGLE -> new SimpleTrigger(new MouseSource(key), ActivationPolicy.Toggle());
                };
            }
            case SCROLL_WHEEL -> {
                // 已在 TriggerIdentity 中保证只能与 TOGGLE 组合
                return new SimpleTrigger(new ScrollSource(key), ActivationPolicy.Toggle());
            }
            default -> throw new IllegalArgumentException("Unsupported TriggerType: " + type);
        }
    }
}
