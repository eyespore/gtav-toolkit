package club.pineclone.gtavops.gui.forked;

import io.vproxy.vfx.control.dialog.VDialogButton;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.font.FontUsages;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.stage.VStage;
import io.vproxy.vfx.util.FXUtils;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 支持设置最小化、最大化控件、以及修复底层按钮层位置问题
 * @param <T> 用户点击之后返回值类型
 */
public class ForkedDialog<T> {

    private static final int BUTTON_HEIGHT = 45;
    private static final int BUTTON_PANE_HEIGHT = BUTTON_HEIGHT + FusionPane.PADDING_V * 2;

    private final VStage vStage;
    private final Label messageLabel = new Label();
    private final Group content = new Group(messageLabel);
    private final FusionPane buttonPane = new FusionPane();
    private final HBox buttonHBox = new HBox();

    protected T returnValue;

    public ForkedDialog() {
        this(new VStage());
    }

    public ForkedDialog(VStage vStage) {
        this.vStage = vStage;
        vStage.getStage().setWidth(900);
        vStage.getStage().centerOnScreen();

        messageLabel.setWrapText(true);
        FontManager.get().setFont(FontUsages.dialogText, messageLabel);
        messageLabel.setTextFill(Theme.current().normalTextColor());

        buttonPane.getContentPane().getChildren().add(buttonHBox);
        buttonPane.getNode().setPrefHeight(BUTTON_PANE_HEIGHT);

        buttonHBox.setAlignment(Pos.CENTER_RIGHT);
        buttonHBox.setSpacing(5);
        FXUtils.observeWidth(buttonPane.getContentPane(), buttonHBox);
        FXUtils.observeWidth(
                vStage.getInitialScene().getScrollPane().getNode(),
                vStage.getInitialScene().getContentPane(),
                -1);
        var root = vStage.getInitialScene().getContentPane();
        root.widthProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            var w = now.doubleValue();
            messageLabel.setPrefWidth(w - 20);
            buttonPane.getNode().setPrefWidth(w - 20);
        });
        root.heightProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            var h = now.doubleValue();
            h = VStage.TITLE_BAR_HEIGHT + h + 10;
            vStage.getStage().setHeight(h);
        });
        FXUtils.forceUpdate(vStage.getStage());
        VPadding vPadding = new VPadding(20);
        HBox hBox = new HBox(
                new HPadding(10),
                new VBox(new VPadding(10),
                        content,
                        vPadding,
                        buttonPane.getNode()
                )
        );
        root.getChildren().add(hBox);
    }

    public void setText(String text) {
        messageLabel.setText(text);
    }

    public Label getMessageNode() {
        return messageLabel;
    }

    public void setButtons(List<ForkedDialogButton<T>> buttons) {
        buttonHBox.getChildren().clear();
        var ls = new ArrayList<Node>();
        for (var btn : buttons) {
            var name = btn.name;
            var button = new FusionButton(name);
            var textBounds = FXUtils.calculateTextBounds(button.getTextNode());
            button.setPrefWidth(Math.max(textBounds.getWidth() + 40, 120));
            button.setPrefHeight(BUTTON_HEIGHT);
            ls.add(button);
            button.setOnAction(e -> {
                if (btn.provider != null) {
                    returnValue = btn.provider.get();
                }
                onButtonClicked(btn);
                vStage.close();
            });
            btn.button = button;
        }
        buttonHBox.getChildren().addAll(ls);
    }

    public Group getCleanContent() {
        content.getChildren().remove(messageLabel);
        return content;
    }

    protected void onButtonClicked(ForkedDialogButton<T> btn) {

    }

    protected void onButtonClicked(VDialogButton<T> btn) {
    }

    public Optional<T> showAndWait() {
        vStage.showAndWait();
        getvStage().temporaryOnTop();
        return Optional.ofNullable(returnValue);
    }

    public VStage getvStage() {
        return vStage;
    }

}
