package club.pineclone.gtavops.common;

import java.util.*;

public abstract class AbstractRegistry<T> {

    private final Map<UUID, T> registry = new LinkedHashMap<>();

    public UUID register(final T t) {
        UUID uuid = UUID.randomUUID();
        registry.put(uuid, t);
        return uuid;
    }

    public T unregister(final UUID uuid) {
        return registry.remove(uuid);
    }

    public T get(final UUID uuid) {
        return registry.get(uuid);
    }

    public Collection<T> values() {
        return registry.values();
    }

    public boolean exists(final UUID uuid) {
        return registry.containsKey(uuid);
    }

}
