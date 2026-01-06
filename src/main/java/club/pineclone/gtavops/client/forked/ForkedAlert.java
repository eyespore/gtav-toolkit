package club.pineclone.gtavops.client.forked;

import io.vproxy.base.util.Logger;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.font.FontUsage;
import io.vproxy.vfx.manager.font.FontUsages;
import io.vproxy.vfx.manager.internal_i18n.InternalI18n;
import io.vproxy.vfx.ui.alert.ThemeAlertBase;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import javafx.scene.control.Alert;
import javafx.stage.Modality;

/* 具备模态能力的 Alert */
public class ForkedAlert extends ThemeAlertBase {

    private ForkedAlert(String title, String contentText, FontUsage fontUsage) {
        Logger.alert("CompatibleAlert: [" + title + "] " + contentText);

        setTitle(title);
        var alertMessage = new ThemeLabel(contentText) {{
            setWrapText(true);
            FontManager.get().setFont(fontUsage, this);
        }};
        FXUtils.observeWidth(getSceneGroup().getNode(), alertMessage, -PADDING_H * 2);
        alertMessagePane.getChildren().add(alertMessage);

        getStage().initModality(Modality.APPLICATION_MODAL);  // 全局模态
    }

    private static String typeToTitle(Alert.AlertType type) {
        if (type == Alert.AlertType.INFORMATION) {
            return InternalI18n.get().alertInfoTitle();
        } else if (type == Alert.AlertType.WARNING) {
            return InternalI18n.get().alertWarningTitle();
        } else if (type == Alert.AlertType.ERROR) {
            return InternalI18n.get().alertErrorTitle();
        } else {
            return type.name();
        }
    }

    public static void show(Alert.AlertType type, String contentText) {
        show(typeToTitle(type), contentText);
    }

    public static void show(Alert.AlertType type, String contentText, FontUsage fontUsage) {
        show(typeToTitle(type), contentText, fontUsage);
    }

    public static void showAndWait(Alert.AlertType type, String contentText) {
        showAndWait(typeToTitle(type), contentText);
    }

    public static void showAndWait(Alert.AlertType type, String contentText, FontUsage fontUsage) {
        showAndWait(typeToTitle(type), contentText, fontUsage);
    }

    public static void show(String title, String contentText) {
        show(title, contentText, FontUsages.alert);
    }

    public static void show(String title, String contentText, FontUsage fontUsage) {
        FXUtils.runOnFX(() -> new ForkedAlert(title, contentText, fontUsage).show());
    }

    public static void showAndWait(String title, String contentText) {
        showAndWait(title, contentText, FontUsages.alert);
    }

    public static void showAndWait(String title, String contentText, FontUsage fontUsage) {
        new ForkedAlert(title, contentText, fontUsage).showAndWait();
    }

}
