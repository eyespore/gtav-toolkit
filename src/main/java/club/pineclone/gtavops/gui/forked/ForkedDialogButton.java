package club.pineclone.gtavops.gui.forked;

import io.vproxy.vfx.ui.button.FusionButton;

import java.util.function.Supplier;

public class ForkedDialogButton<T> {
    public final String name;
    public final Supplier<T> provider;
    FusionButton button;

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

    // will be null before setting into a VDialog
    public FusionButton getButton() {
        return button;
    }
}