package club.pineclone.gtavops.client.scene;

import club.pineclone.gtavops.config.MacroConfigLoader;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.utils.ImageUtils;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.text.FontWeight;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class IntroScene extends SceneTemplate {

    public IntroScene(ExtendedI18n i18n) {
        super(i18n);
        ExtendedI18n.IntroScene iI18n = i18n.introScene;

        enableAutoContentWidthHeight();
        setBackgroundImage(ImageUtils.loadImage("/img/bg1.png"));

        // TODO: 通过向后端请求获取version而不是读取配置文件
        String version = "unknown";
        String versionFile = "/version.txt";
        try (InputStream in = MacroConfigLoader.class.getResourceAsStream(versionFile)) {
            if (in != null) {
                version = new String(in.readAllBytes(), StandardCharsets.UTF_8).trim();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var vBox = new VBox(
                new ThemeLabel(iI18n.header) {{
                    FontManager.get().setFont(this, settings -> settings.setSize(30));
                }},
                new VPadding(10),
                new ThemeLabel(iI18n.versionLabel + version) {{
                    FontManager.get().setFont(this, settings -> settings.setSize(23));
                }},
                new VPadding(10),
                new ThemeLabel(iI18n.acknowledgement) {{
                    FontManager.get().setFont(this, settings -> settings.setSize(23).setWeight(FontWeight.BOLD));
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
        return i18n.introScene.title;
    }
}
