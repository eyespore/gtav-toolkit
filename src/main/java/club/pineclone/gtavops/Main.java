package club.pineclone.gtavops;

import club.pineclone.gtavops.gui.theme.PinecloneTheme;
import io.vproxy.vfx.theme.Theme;
import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        Theme.setTheme(new PinecloneTheme());
        Application.launch(MainFX.class, args);
    }
}
