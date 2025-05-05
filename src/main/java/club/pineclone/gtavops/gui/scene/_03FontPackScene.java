package club.pineclone.gtavops.gui.scene;

import club.pineclone.gtavops.config.ConfigHolder;
import club.pineclone.gtavops.config.Configuration;
import club.pineclone.gtavops.gui.component.VTextField;
import club.pineclone.gtavops.gui.forked.*;
import club.pineclone.gtavops.gui.theme.GTAVOpsBaseTheme;
import club.pineclone.gtavops.pojo.FontpackMetadata;
import club.pineclone.gtavops.i18n.ExtendedI18n;
import club.pineclone.gtavops.i18n.I18nHolder;
import club.pineclone.gtavops.service.FontpackService;
import club.pineclone.gtavops.utils.ColorUtils;
import club.pineclone.gtavops.utils.PathUtils;
import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import io.vproxy.base.util.callback.Callback;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.font.FontUsages;
import io.vproxy.vfx.ui.alert.StackTraceAlert;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.loading.LoadingFailure;
import io.vproxy.vfx.ui.loading.LoadingItem;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.table.VTableView;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import io.vproxy.vfx.util.MiscUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class _03FontPackScene extends SceneTemplate {

    private final Configuration config;

    private final ExtendedI18n i18n;
    private final ExtendedI18n.FontPack fpI18n;

    private final FontpackService fontpackService;
    private final VTableView<FontpackMetadata> table;  /* 字体包元数据表格 */

    private final FusionButton gameHomeChooseBtn;
    private final FusionButton fontpackToggleBtn;

    public _03FontPackScene() {
        super(VSceneRole.MAIN);
        i18n = I18nHolder.get();
        fpI18n = i18n.fontPack;

        config = ConfigHolder.get();
        fontpackService = FontpackService.getInstance();
        enableAutoContentWidthHeight();

        ThemeLabel pathLabel = new ThemeLabel(fpI18n.gamePath);
        HBox pathLabelContent = new HBox(10);
        pathLabelContent.setPadding(new Insets(24, 0, 0, 0));

        gameHomeChooseBtn = new FusionButton() {{
            setPrefWidth(700);
            setPrefHeight(35);
            setOnAction(e -> selectGameHome());
            setDisableAnimation(true);

            String gameHome = config.gameHome;
            if (gameHome == null || gameHome.isEmpty()) {
                setText(fpI18n.emptyGameHome);
            } else {
                setText(gameHome);
            }
        }};
        pathLabelContent.getChildren().addAll(pathLabel, gameHomeChooseBtn);
        pathLabelContent.setAlignment(Pos.CENTER);
        pathLabelContent.setLayoutY(10);
        FXUtils.observeWidthCenter(getContentPane(), pathLabelContent);

        /* CREATE TABLE */
        table = new VTableView<>();
        table.getNode().setPrefWidth(1000);
        table.getNode().setPrefHeight(480);

//        var idCol = new ForkedTableColumn<FontPackMetadata, Object>("id", data -> data.id);
        var nameCol = new ForkedTableColumn<FontpackMetadata, FontpackMetadata>(fpI18n.name, Function.identity());
        var enabledCol = new ForkedTableColumn<FontpackMetadata, FontpackMetadata>(fpI18n.status, Function.identity());
        var typeCol = new ForkedTableColumn<>(fpI18n.type, FontpackMetadata::formatType);
        var sizeCol = new ForkedTableColumn<>(fpI18n.size, FontpackMetadata::formatSize);
        var createTimeCol = new ForkedTableColumn<>(fpI18n.createAt, FontpackMetadata::formatCreatedAt);

//        idCol.setMinWidth(300);
        nameCol.setPrefWidth(400);
        nameCol.setAlignment(Pos.CENTER);
        nameCol.setComparator(Comparator.comparing(FontpackMetadata::getName));
        nameCol.setNodeBuilder(data -> {
            var textField = new VTextField();
            var text = new ForkedFusionW(textField) {{
                FontManager.get().setFont(FontUsages.tableCellText, getLabel());
                String colorStr = ColorUtils.formatAsHex(((GTAVOpsBaseTheme)GTAVOpsBaseTheme.current()).activeTextColor());
                node.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorStr + "; -fx-padding: 8 0 8 0; -fx-border-color: transparent");
            }};

            textField.setText(data.getName());
            textField.focusedProperty().addListener((ob, old, now) -> {
                if (old == null || now == null) return;
                if (old && !now) {
                    /* 用户修改字体包名称 */
                    String oldName = data.getName();
                    if (!oldName.equals(textField.getText())) {
                        data.setName(textField.getText());
                        try {
                            boolean flag = fontpackService.updateFontPack(data);
                            if (!flag) {
                                /* 更新失败 */
                                data.setName(oldName);
                                Logger.error(LogType.SYS_ERROR, "user try renaming a fontpack but it cannot be found");
                            }
                        } catch (IOException e) {
                            /* 更新失败 */
                            Logger.error(LogType.FILE_ERROR, e.getMessage());
                        }
                    }
                }
            });
            return text;
        });

        enabledCol.setAlignment(Pos.CENTER);
        enabledCol.setComparator(Comparator.comparing(FontpackMetadata::getEnabled).reversed());
        enabledCol.setNodeBuilder(data -> {
            var textField = new ThemeLabel(data.formatEnabled());
            if (data.getEnabled()) {
                textField.setStyle("-fx-text-fill: lightblue");
            }
            return textField;
        });

        typeCol.setAlignment(Pos.CENTER);
        typeCol.setComparator(String::compareTo);

        sizeCol.setAlignment(Pos.CENTER);
        sizeCol.setComparator(String::compareTo);

        createTimeCol.setMinWidth(250);
        createTimeCol.setAlignment(Pos.CENTER);
        createTimeCol.setTextBuilder(MiscUtils.YYYYMMddHHiissDateTimeFormatter::format);
        createTimeCol.setComparator(ZonedDateTime::compareTo);

        //noinspection unchecked
        table.getColumns().addAll(nameCol, enabledCol ,typeCol, sizeCol, createTimeCol);
        FusionPane controlPane = new FusionPane(false);

        /* 字体包开关按钮 */
        fontpackToggleBtn = new FusionButton(fpI18n.activateFontpack) {{
            setOnAction(e -> {
                var selected = table.getSelectedItem();
                if (selected == null) {
                    return;
                }

                if (config.gameHome == null || config.gameHome.isEmpty()) {
                    /* 目录不存在，提醒用户选择目录 */
                    ForkedDialog<Integer> dialog = ForkedDialog.confirmDialog(
                            fpI18n.emptyGameHomeAlert, Modality.APPLICATION_MODAL
                    );
                    Optional<Integer> result = dialog.showAndWait();
                    if (result.filter(i -> i == ForkedDialog.CANCEL).isPresent()) return;

                    boolean flag = selectGameHome();
                    if (!flag) return;  /* 选择路径失败 */
                }

                ForkedDialog<Integer> dialog = ForkedDialog.confirmDialog(
                        MessageFormat.format(fpI18n.confirmActivateFontpack, selected.getName()),
                        Modality.APPLICATION_MODAL
                );

                /* 询问用户是否激活字体包 */
                Optional<Integer> result = dialog.showAndWait();
                if (result.filter(i -> i == ForkedDialog.CONFIRM).isPresent()) {

                    /* 用户选择激活字体包，尝试激活字体包 */
                    Path source = PathUtils.getFontpacksBaseDirPath().resolve(selected.getId()).resolve("update.rpf");
                    Path target = Path.of(config.gameHome).resolve("update").resolve("update.rpf");

                    try {
//                    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                        asyncCopy(source, target, new Callback<>() {
                            @Override
                            protected void onSucceeded(Void unused) {
                                FXUtils.runDelay(20, () ->
                                        ForkedAlert.showAndWait(Alert.AlertType.INFORMATION, fpI18n.importSuccess));

                                /*拷贝成功 变更状态 */
                                updateAsEnableFontpack(selected);
                            }

                            @Override
                            protected void onFailed(LoadingFailure loadingFailure) {
                                FXUtils.runDelay(20, () ->
                                        ForkedAlert.showAndWait(Alert.AlertType.ERROR, loadingFailure.getMessage()));
                                StackTraceAlert.showAndWait(loadingFailure.getCause());
                            }
                        }, () -> {});

                    } catch (IOException ex) {
                        /* 拷贝失败 */
                        StackTraceAlert.showAndWait(ex);
                        Logger.error(LogType.FILE_ERROR, ex.getMessage());
                    }
                }
            });
            setPrefWidth(120);
            setPrefHeight(40);
        }};

        table.getNode().setOnMouseClicked(event -> {
            FontpackMetadata selectedItem = table.getSelectedItem();
            if (selectedItem != null && selectedItem.getEnabled()) {
                fontpackToggleBtn.setDisable(true);
                fontpackToggleBtn.setText(fpI18n.alreadyActivated);
            } else {
                fontpackToggleBtn.setDisable(false);
                fontpackToggleBtn.setText(fpI18n.activateFontpack);
            }
        });

        controlPane.getContentPane().getChildren().add(new VBox(
                /* 启用字体包 */
                fontpackToggleBtn,
                new VPadding(10),
                /* 导入字体包 */
                new FusionButton(fpI18n.importFontpack) {{
                    setOnAction(e -> {
                        /* 让用户选择字体包文件 */
                        File selectedFile = selectFontpackFile();
                        if (selectedFile == null) return;

                        Optional<String> fontpackName = readFontpackNameFromUser();
                        fontpackName.ifPresent(s -> createFontpack(s, false, selectedFile.toPath()));
                    });
                    setPrefWidth(120);
                    setPrefHeight(40);
                }},
                new VPadding(10),

                /* 删除字体包 */
                new FusionButton(fpI18n.removeFontpack) {{
                    setOnAction(e -> {
                        var selected = table.getSelectedItem();
                        if (selected == null) return;

                        if (selected.getEnabled()) {
                            /* 当前字体包被启用，禁止删除 */
                            ForkedAlert.showAndWait(Alert.AlertType.WARNING, fpI18n.fontpackIsEnabled);
                            return;
                        }

                        /* 询问用户是否删除 */
                        ForkedDialog<Integer> dialog = ForkedDialog.confirmDialog(
                                MessageFormat.format(fpI18n.confirmRemoveFontpack, selected.getName()),
                                Modality.APPLICATION_MODAL
                        );
                        Optional<Integer> result = dialog.showAndWait();

                        if (result.filter(i -> i == ForkedDialog.CONFIRM).isPresent()) {
                            try {
                                boolean flag = fontpackService.deleteFontPack(selected.getId());
                                if (flag) {
                                    table.getItems().remove(selected);
                                } else {
                                    Logger.error(LogType.SYS_ERROR, "user trying deleting a fontpack but not exist");
                                }
                            } catch (IOException ex) {
                                Logger.error(LogType.FILE_ERROR, ex.getMessage());
                            }
                        }
                    });
                    setPrefWidth(120);
                    setPrefHeight(40);
                }}
        ));

        HBox hbox = new HBox(table.getNode(), new HPadding(10), controlPane.getNode());
        FXUtils.observeWidthCenter(getContentPane(), hbox);
        hbox.setLayoutY(100);

        getContentPane().getChildren().addAll(pathLabelContent, hbox);
        refreshTable();  /* 刷新列表 */
    }

    /* 将selected设置为激活状态，同时协调其他的字体包状态 */
    private void updateAsEnableFontpack(FontpackMetadata selected) {
        List<FontpackMetadata> result = fontpackService.listFontPacksByCondition(FontpackMetadata.builder().enabled(true).build());
        result.forEach(f -> {
            f.setEnabled(false);
            try {
                fontpackService.updateFontPack(f);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        selected.setEnabled(true);
        try {
            fontpackService.updateFontPack(selected);  /* 改激活状态 */
            refreshTable();

        } catch (IOException ex) {
            StackTraceAlert.showAndWait(ex);
        }
    }

    private boolean selectGameHome() {
        /* 让用户选择游戏目录 */
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(fpI18n.chooseGameHome);
        File file = directoryChooser.showDialog(this.getContentPane().getScene().getWindow());

        if (file == null) return false;

        String absPath = file.getAbsolutePath();
        Path path = Path.of(absPath);

        /* 确保目录存在 */
        Path updateDir = path.resolve("update");
        if (Files.notExists(updateDir)) {
            /* update目录不存在，鉴定为错误的游戏目录 */
            ForkedAlert.showAndWait(Alert.AlertType.WARNING, fpI18n.illegalGameHome);
            return false;
        }

        Path originalFontpack = updateDir.resolve("update.rpf");
        /* 检查是否存在原始字体包 */
        if (Files.exists(originalFontpack)) {
            /* 存在原始字体包，询问是否保存 */
            ForkedDialog<Integer> dialog = ForkedDialog.confirmDialog(
                    MessageFormat.format(fpI18n.fontpackExisted, originalFontpack.toAbsolutePath().toString()),
                    Modality.APPLICATION_MODAL
            );

            Optional<Integer> result = dialog.showAndWait();
            if (result.filter(i -> i == ForkedDialog.CONFIRM).isPresent()) {
                /* 用户确认保存字体包，键入字体包名称 */
                Optional<String> fontpackName = readFontpackNameFromUser();
                fontpackName.ifPresent(s -> createFontpack(s, true, originalFontpack));
            }
        }
        config.gameHome = absPath;
        gameHomeChooseBtn.setText(absPath);
        return true;
    }

    private Optional<String> readFontpackNameFromUser() {
        ForkedDialog<Integer> dialog = ForkedDialog.confirmDialog(Modality.APPLICATION_MODAL);

        dialog.getVStage().getStage().setWidth(600);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");;
        String defaultFontpackName = fpI18n.defaultNaming + ZonedDateTime.now().format(formatter);

        HBox content = dialog.getBody();
        content.setAlignment(Pos.CENTER_LEFT);

        ThemeLabel fontpackNameLabel = new ThemeLabel(fpI18n.fontpackName + ": ");
        VTextField fontpackTextField = new VTextField() {{
            setPromptText(defaultFontpackName);
            setPrefWidth(350);
        }};

        FXUtils.observeWidth(dialog.getVStage().getRootSceneGroup().getNode(), content, -20);
        content.getChildren().addAll(
                fontpackNameLabel,
                fontpackTextField);

        Optional<Integer> result = dialog.showAndWait();
        if (result.filter(i -> i == ForkedDialog.CONFIRM).isPresent()) {
            String fontpackName = fontpackTextField.getText();

            if (fontpackName == null || fontpackName.trim().isEmpty()) {
                fontpackName = defaultFontpackName;
            }
            return Optional.of(fontpackName);
        }
        return Optional.empty();
    }

    private void createFontpack(String name, boolean enabled, Path source) {
        try {
            long size = source.toFile().length();
            FontpackMetadata newFontpack = fontpackService.createFontPack(name, enabled,"", 0, size);  /* 将信息写入数据库 */
            Path target = PathUtils.getFontpacksBaseDirPath().resolve(newFontpack.getId()).resolve("update.rpf");  /* 拷贝文件到目标目录 */
//            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);  /* 将文件拷贝到目标目录 */
            asyncCopy(source, target, new Callback<>() {
                @Override
                protected void onSucceeded(Void unused) {
                    FXUtils.runDelay(20, () ->
                            ForkedAlert.showAndWait(Alert.AlertType.INFORMATION, fpI18n.importSuccess));
                    updateAsEnableFontpack(newFontpack);
                }

                @Override
                protected void onFailed(LoadingFailure loadingFailure) {
                    FXUtils.runDelay(20, () ->
                            ForkedAlert.showAndWait(Alert.AlertType.ERROR, loadingFailure.getMessage()));
                    try {
                        fontpackService.deleteFontPack(newFontpack.getId());  /* 清理文件 */
                    } catch (IOException e) {
                        StackTraceAlert.showAndWait(e);
                    }
                }
            }, () -> {});

        } catch (IOException ex) {
            /* 创建字体包出错，回滚 */
            Logger.error(LogType.FILE_ERROR, ex.getMessage());
        }
    }

    private void asyncCopy(Path source, Path target, Callback<Void, LoadingFailure> cb, Runnable doFinally) throws IOException {
        var stage = new ForkedLoadingStage(fpI18n.importingFontpack);
        stage.getStage().initModality(Modality.APPLICATION_MODAL);

        List<LoadingItem> loadingItems = new ArrayList<>();

        // 获取文件总大小
        int partSize = 8192;
        long fileSize = source.toFile().length();
        long partCount = (fileSize + partSize - 1) / partSize;

        InputStream in = new BufferedInputStream(new FileInputStream(source.toFile()));
        OutputStream out = new BufferedOutputStream(new FileOutputStream(target.toFile()));

        for (int i = 0; i < partCount; i++) {
            loadingItems.add(createLoadingItem(in, out, partSize));
        }

        stage.setItems(loadingItems);
        stage.load(new Callback<>() {
            @Override
            protected void onSucceeded(Void unused) {
                cb.succeeded();
            }

            @Override
            protected void onFailed(LoadingFailure loadingFailure) {
                cb.failed(loadingFailure);
            }

            @Override
            protected void doFinally() {
                try {
                    in.close();
                    out.close();
                } catch (IOException ex) {
                    StackTraceAlert.showAndWait(fpI18n.importFailure, ex);
                    Logger.error(LogType.FILE_ERROR, ex.getMessage());
                } finally {
                    stage.close();
                    doFinally.run();
                }
            }
        });
    }

    /* 将每一次拷贝构建为一个分片任务 */
    private LoadingItem createLoadingItem(InputStream in, OutputStream out, int partSize) {
        return new LoadingItem(1, fpI18n.importingFontpackDesc, () -> {
            byte[] buffer = new byte[8192];
            int read, totalRead = 0;
            try {
                while (totalRead < partSize) {
                    int toRead = Math.min(partSize - totalRead, buffer.length);
                    read = in.read(buffer, 0, toRead);
                    if (read == -1) break;

                    out.write(buffer, 0, read);
                    totalRead += read;
                }
            } catch (IOException ex) {
                return false;
            }
            return true;
        });
    }

    private File selectFontpackFile() {
        /* 让用户选择字体包文件 */
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(fpI18n.chooseFontpackFile);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(fpI18n.fontpackFileDesc, "*.rpf")
        );
        return fileChooser.showOpenDialog(this.getContentPane().getScene().getWindow());
    }

    /* 刷新表 */
    private void refreshTable() {
        table.setItems(fontpackService.listFontPacks());
    }

    @Override
    public String getTitle() {
        return I18nHolder.get().fontPack.title;
    }
}
