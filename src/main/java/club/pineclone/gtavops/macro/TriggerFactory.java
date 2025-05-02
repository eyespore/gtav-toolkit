package club.pineclone.gtavops.macro;

import club.pineclone.gtavops.macro.trigger.*;
import io.vproxy.vfx.entity.input.Key;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

public class TriggerFactory {

    /** 用来存放已创建的 CompositeTrigger，键为 id */
    private static final ConcurrentMap<Integer, Trigger> registry = new ConcurrentHashMap<>();

    private TriggerFactory() { }

    public static Trigger getTrigger(TriggerIdentity... identities) {
        if (identities.length == 1) {
            TriggerIdentity identity = identities[0];
            int id = identity.hashCode();
            // 如果已经存在则直接返回，否则创建并注册
            return registry.computeIfAbsent(id, key -> createSimpleTrigger(identity));
        } else {
            List<Trigger> list = Stream.of(identities).map(TriggerFactory::createSimpleTrigger).toList();
            return new KeyChordTrigger(list);
        }
    }

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
