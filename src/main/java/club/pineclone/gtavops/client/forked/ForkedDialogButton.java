package club.pineclone.gtavops.client.forked;

import io.vproxy.vfx.ui.button.FusionButton;
import lombok.Getter;

import java.util.function.Supplier;

public class ForkedDialogButton<T> {
    public final String name;
    public final Supplier<T> provider;
    @Getter FusionButton button;

    public ForkedDialogButton(String name, T value) {
        this(name, () -> value);
    }

    public ForkedDialogButton(String name, Supplier<T> provider) {
        this.name = name;
        this.provider = provider;
    }

    public ForkedDialogButton(String name) {
        this.name = name;
        this.provider = null;
    }
}