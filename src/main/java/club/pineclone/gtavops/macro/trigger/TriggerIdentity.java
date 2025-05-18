package club.pineclone.gtavops.macro.trigger;

import io.vproxy.vfx.entity.input.Key;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class TriggerIdentity {

    private final TriggerType type;
    private final TriggerMode mode;
    private final Set<Key> keys = new HashSet<>();

    /**
     * 创建一个触发器标识符
     * @param mode 触发模式
     * @param keys 触发按键，支持多按键触发
     */
    public TriggerIdentity(TriggerMode mode, Key... keys) {
        this(mode, Arrays.stream(keys).collect(Collectors.toSet()));
    }

    public TriggerIdentity(TriggerMode mode, Set<Key> keys) {
        this.type = checkKeyType(keys);  /* 检查类型 */
        keys.forEach(k -> this.checkKeyCompatibility(k, mode));  /* 检查合法性 */
        this.mode = mode;
        this.keys.addAll(keys);
    }

    private void checkKeyCompatibility(Key key, TriggerMode mode) {
        TriggerType type = TriggerType.of(key);
        if (type == TriggerType.SCROLL_WHEEL && mode == TriggerMode.HOLD) {
            throw new IllegalArgumentException("Mouse wheel can only work with TriggerMode.TOGGLE.");
        }
    }

    /* 获取key的类型，要求一个Trigger触发的key至少为同一类型 */
    public TriggerType checkKeyType(Set<Key> keys) {
        if (keys.isEmpty()) {
            throw new IllegalArgumentException("Key set is empty.");
        }

        Iterator<Key> iterator = keys.iterator();
        TriggerType type = TriggerType.of(iterator.next());

        while (iterator.hasNext()) {
            if (TriggerType.of(iterator.next()) != type) {
                throw new IllegalArgumentException("Keys have inconsistent TriggerTypes.");
            }
        }

        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TriggerIdentity that = (TriggerIdentity) o;
        return type == that.type && mode == that.mode && Objects.equals(keys, that.keys);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, mode, keys);
    }

}
