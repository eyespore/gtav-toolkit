package club.pineclone.gtavops.gui.forked;

import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.i18n.I18nHolder;
import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import io.vproxy.vfx.control.scroll.VScrollPane;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.font.FontUsages;
import io.vproxy.vfx.manager.internal_i18n.InternalI18n;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.pane.ClickableFusionPane;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.stage.VStage;
import io.vproxy.vfx.ui.stage.VStageInitParams;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import lombok.Getter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import static io.vproxy.vfx.ui.pane.FusionPane.PADDING_H;

/**
 * 支持设置最小化、最大化控件、以及修复底层按钮层位置问题
 * @param <T> 用户点击之后返回值类型
 */
public class ForkedDialog<T> {

    private static final int BUTTON_HEIGHT = 45;
    private static final int BUTTON_PANE_HEIGHT = BUTTON_HEIGHT + FusionPane.PADDING_V * 2;

    @Getter private final VStage vStage;
    @Getter private final Label headerLabel = new Label();
    @Getter private final Group header = new Group(headerLabel);
    @Getter private final HBox body = new HBox(10);
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
//        vStage.getInitialScene().enableAutoContentWidthHeight();

//        FXUtils.observeWidth(vStage.getRootSceneGroup().getNode(), this.content);

        headerLabel.setWrapText(true);
        FontManager.get().setFont(FontUsages.dialogText, headerLabel);
        headerLabel.setTextFill(Theme.current().normalTextColor());

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
            headerLabel.setPrefWidth(w - 20);
            buttonPane.getNode().setPrefWidth(w - 20);
        });
        root.heightProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            var h = now.doubleValue();
            h = VStage.TITLE_BAR_HEIGHT + h + 10;
            vStage.getStage().setHeight(h);
        });
        FXUtils.forceUpdate(vStage.getStage());
        HBox hBox = new HBox(
                new HPadding(10),
                new VBox(new VPadding(10),
                        header,
                        body,
                        new VPadding(20),
                        buttonPane.getNode()
                )
        );
        root.getChildren().add(hBox);
    }

    public void setHeaderText(String text) {
        headerLabel.setText(text);
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
                vStage.close();
            });
            btn.button = button;
        }
        buttonHBox.getChildren().addAll(ls);
    }

    public Group getCleanBody() {
        header.getChildren().remove(headerLabel);
        return header;
    }

    public Optional<T> showAndWait() {
        vStage.showAndWait();
        getVStage().temporaryOnTop();
        return Optional.ofNullable(returnValue);
    }

    public static final int NONE = 0;
    public static final int CONFIRM = 1;
    public static final int CANCEL = 2;

    /* 确认取消弹窗 1: 确认  0: 取消 */
    public static ForkedDialog<Integer> simpleDialog() {
        return simpleDialog(null, Modality.NONE);
    }

    public static ForkedDialog<Integer> simpleDialog(String text) {
        return simpleDialog(text, Modality.NONE);
    }

    public static ForkedDialog<Integer> simpleDialog(Modality modality) {
        return simpleDialog(null, modality);
    }

    public static ForkedDialog<Integer> simpleDialog(String text, Modality modality) {
        ForkedDialog<Integer> dialog = createConfirmCancelDialog();
        dialog.setHeaderText(text);
        dialog.getVStage().getStage().initModality(modality);
        return dialog;
    }

    public static ForkedDialog<Integer> simpleDialog(String text, Modality modality, int flag) {
        ForkedDialog<Integer> dialog = createSimpleDialog(flag);
        dialog.setHeaderText(text);
        dialog.getVStage().getStage().initModality(modality);
        return dialog;
    }

    private static ForkedDialog<Integer> createConfirmCancelDialog() {
        return createSimpleDialog(CONFIRM | CANCEL);
    }

    private static ForkedDialog<Integer> createSimpleDialog(int flag) {
        return createDialog(flag);
    }

    private static ForkedDialog<Integer> createDialog(int flags) {
        ExtendedI18n i18n = I18nHolder.get();
        ForkedDialog<Integer> dialog = new ForkedDialog<>(new VStage(
                new VStageInitParams().setIconifyButton(false).setMaximizeAndResetButton(false).setResizable(false)
        ));
        List<ForkedDialogButton<Integer>> buttons = new ArrayList<>();

        if ((flags & CONFIRM) != 0) {
            buttons.add(new ForkedDialogButton<>(i18n.confirm, CONFIRM));
        }

        if ((flags & CANCEL) != 0) {
            buttons.add(new ForkedDialogButton<>(i18n.cancel, CANCEL));
        }

        dialog.setButtons(buttons);
        return dialog;
    }

    public static ForkedDialog<Integer> stackTraceDialog(Throwable throwable, int flags) {
        return stackTraceDialog("", throwable, flags);
    }

    public static ForkedDialog<Integer> stackTraceDialog(Throwable throwable) {
        return stackTraceDialog("", throwable, CONFIRM | CANCEL);
    }

    public static ForkedDialog<Integer> stackTraceDialog(String desc, Throwable throwable) {
        return stackTraceDialog(desc, throwable, CONFIRM | CANCEL);
    }

    public static ForkedDialog<Integer> stackTraceDialog(String desc, Throwable throwable, int flags) {
        Logger.error(LogType.ALERT, "StackTraceAlert: " + desc, throwable);
        ForkedDialog<Integer> dialog = createDialog(flags);
        dialog.getVStage().getStage().initModality(Modality.APPLICATION_MODAL);

        dialog.setHeaderText(InternalI18n.get().stacktraceAlertTitle());
        var headerText = new ThemeLabel(InternalI18n.get().stacktraceAlertHeaderText()) {{
            FontManager.get().setFont(FontUsages.alert, this);
        }};
        var descText = new ThemeLabel() {{
            FontManager.get().setFont(FontUsages.alert, this);
        }};
        if (desc != null && !desc.isBlank()) {
            descText.setText(desc);
        }

        var aboutStacktraceText = new ThemeLabel(InternalI18n.get().stacktraceAlertLabel()) {{
            FontManager.get().setFont(FontUsages.alert, this);
        }};

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String exceptionText = sw.toString();

        var stacktracePane = new ClickableFusionPane();
        stacktracePane.setOnAction(e -> {
            Clipboard.getSystemClipboard().setContent(
                    Map.of(DataFormat.PLAIN_TEXT, exceptionText)
            );
        });

        stacktracePane.getNode().setPrefWidth(dialog.getVStage().getStage().getWidth() - 2 * PADDING_H - 5);
        var stacktraceText = new ThemeLabel(exceptionText) {{
            setFont(new Font(FontManager.FONT_NAME_JetBrainsMono, 14));
            setWrapText(true);
            setPrefWidth(stacktracePane.getNode().getPrefWidth() - PADDING_H * 2);
        }};

        stacktracePane.getContentPane().getChildren().add(stacktraceText);
        FXUtils.observeHeight(stacktraceText, stacktracePane.getNode(), FusionPane.PADDING_V * 2);

        // this triggers height update
        FXUtils.runDelay(50, () -> stacktraceText.setMinHeight(stacktraceText.getHeight() + 1));

        VBox alertMessagePane = new VBox();

        var scrollPaneContent = new VScrollPane();
        scrollPaneContent.getNode().setPrefWidth(stacktracePane.getNode().getPrefWidth());
        scrollPaneContent.getNode().setPrefHeight(400);
        scrollPaneContent.setContent(stacktracePane.getNode());

        alertMessagePane.getChildren().addAll(
                headerText,
                descText,
                new VPadding(20),
                aboutStacktraceText,
                scrollPaneContent.getNode()
//                stacktracePane.getNode()
        );
        dialog.getBody().getChildren().add(alertMessagePane);
        return dialog;
    }

}
