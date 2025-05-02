package club.pineclone.gtavops.gui.scene;

import club.pineclone.gtavops.config.ConfigHolder;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.i18n.I18nHolder;
import club.pineclone.gtavops.utils.ImageUtils;
import io.vproxy.commons.util.Singleton;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.text.FontWeight;

public class _01IntroScene extends SceneTemplate {

    public _01IntroScene() {
        super(VSceneRole.MAIN);
        enableAutoContentWidthHeight();
        setBackgroundImage(ImageUtils.loadImage("/img/bg1.png"));

        ExtendedI18n i18n = I18nHolder.get();
        var vBox = new VBox(
                new ThemeLabel(i18n.introSceneHeader) {{
                    FontManager.get().setFont(this, settings -> settings.setSize(40));
                }},
                new VPadding(10),
                new ThemeLabel(i18n.versionLabel + ConfigHolder.APPLICATION_VERSION) {{
                    FontManager.get().setFont(this, settings -> settings.setSize(28));
                }},
                new VPadding(10),
                new ThemeLabel(i18n.acknowledgement) {{
                    FontManager.get().setFont(this, settings -> settings.setSize(30).setWeight(FontWeight.BOLD));
                }}
        ) {{
            setAlignment(Pos.CENTER);
        }};

        vBox.setPadding(new Insets(0, 470, 0, 0));
        getContentPane().getChildren().add(vBox);
        FXUtils.observeWidthHeightCenter(getContentPane(), vBox);
    }

    @Override
    public String getTitle() {
        return I18nHolder.get().introSceneTitle;
    }
}
