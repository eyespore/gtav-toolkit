package club.pineclone.gtavops.macro.trigger;

import java.util.Objects;
import java.util.Set;

public class TriggerIdentityGroup {

    private final Set<TriggerIdentity> identities;

    public TriggerIdentityGroup(TriggerIdentity... identities) {
        this.identities = Set.of(identities);
    }

    public TriggerIdentityGroup(Set<TriggerIdentity> identities) {
        this.identities = identities;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TriggerIdentityGroup)) return false;
        return Objects.equals(identities, ((TriggerIdentityGroup) obj).identities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identities);
    }
}
