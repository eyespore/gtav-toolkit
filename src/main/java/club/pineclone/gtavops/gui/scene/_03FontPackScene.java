package club.pineclone.gtavops.gui.scene;

import club.pineclone.gtavops.config.ConfigHolder;
import club.pineclone.gtavops.config.Config;
import club.pineclone.gtavops.gui.component.VTextField;
import club.pineclone.gtavops.gui.forked.*;
import club.pineclone.gtavops.gui.theme.BaseTheme;
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

    private final Config config;

    private final ExtendedI18n i18n;
    private final ExtendedI18n.FontPack fpI18n;

    private final FontpackService fontpackService;
    private final VTableView<FontpackMetadata> table;  /* 字体包元数据表格 */

    private final FusionButton gameHomeChooseBtn;
    private final FusionButton fontpackToggleBtn;

    private File update1File;  /* 缓存 */
    private File update2File;

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
                String colorStr = ColorUtils.formatAsHex(((BaseTheme) BaseTheme.current()).activeTextColor());
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

        /* 激活字体包按钮 */
        fontpackToggleBtn = new FusionButton(fpI18n.activateFontpack) {{
            setOnAction(e -> {
                var selected = table.getSelectedItem();
                if (selected == null) {
                    return;
                }

                if (config.gameHome == null || config.gameHome.isEmpty()) {
                    /* 未选择家目录，提醒用户选择目录 */
                    ForkedDialog.simpleDialog(
                            fpI18n.emptyGameHomeAlert, Modality.APPLICATION_MODAL, ForkedDialog.CONFIRM
                    ).showAndWait();
                    return;
                }

                ForkedDialog<Integer> dialog = ForkedDialog.simpleDialog(
                        MessageFormat.format(fpI18n.confirmActivateFontpack, selected.getName()),
                        Modality.APPLICATION_MODAL
                );

                /* 询问用户是否激活字体包 */
                Optional<Integer> result = dialog.showAndWait();
                if (result.filter(i -> i == ForkedDialog.CONFIRM).isPresent()) {
                    /* 用户选择激活字体包 */
                    int structure = selected.getStructure();
                    boolean flag = false;
                    if (structure == 2) {
                        flag = overloadFontpack(selected);
                    } else if (structure == 0 || structure == 1) {
                        flag = overloadFontpack(selected, structure);
                    }
                    if (!flag) {
                        /* 拷贝失败 */
                        ForkedDialog.simpleDialog(fpI18n.importFailure, Modality.APPLICATION_MODAL, ForkedDialog.CONFIRM).showAndWait();
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
                        update1File = null;  /* 清除缓存 */
                        update2File = null;

                        ForkedDialog<Integer> dialog = ForkedDialog.simpleDialog(Modality.APPLICATION_MODAL);
                        dialog.getVStage().getStage().setWidth(600);
                        dialog.setHeaderText(fpI18n.chooseFontpackResource + ": ");

                        HBox content = dialog.getBody();
                        content.setAlignment(Pos.CENTER_LEFT);

                        HBox update1FileChooseContent = new HBox(10);
                        update1FileChooseContent.setPadding(new Insets(24, 0, 0, 0));

                        ThemeLabel update1FileLabel = new ThemeLabel(fpI18n.update1File);

                        FusionButton update1FileChooseBtn = new FusionButton() {{
                            setPrefWidth(400);
                            setPrefHeight(35);
                            setOnAction(e -> selectFontpackFile(fpI18n.chooseUpdate1File).ifPresent(f -> {
                                update1File = f;
                                setText(update1File.getAbsolutePath());
                            }));
                            setDisableAnimation(true);
                        }};

                        update1FileChooseBtn.setText(fpI18n.update1FileBtnText);
                        update1FileChooseContent.getChildren().addAll(update1FileLabel, update1FileChooseBtn);
                        update1FileChooseContent.setAlignment(Pos.CENTER);
                        update1FileChooseContent.setLayoutY(10);
                        FXUtils.observeWidth(dialog.getVStage().getRoot().getContentPane(), update1FileChooseContent, -20);

                        HBox update2FileChooseContent = new HBox(10);
                        update2FileChooseContent.setPadding(new Insets(24, 0, 0, 0));

                        ThemeLabel update2FileLabel = new ThemeLabel(fpI18n.update2File);
                        FusionButton update2FileChooseBtn = new FusionButton() {{
                            setPrefWidth(400);
                            setPrefHeight(35);
                            setOnAction(e -> selectFontpackFile(fpI18n.chooseUpdate2File).ifPresent(f -> {
                                update2File = f;
                                setText(update2File.getAbsolutePath());
                            }));
                            setDisableAnimation(true);
                        }};

                        update2FileChooseBtn.setText(fpI18n.update2FileBtnText);
                        update2FileChooseContent.getChildren().addAll(update2FileLabel, update2FileChooseBtn);
                        update2FileChooseContent.setAlignment(Pos.CENTER);
                        update2FileChooseContent.setLayoutY(10);
                        FXUtils.observeWidth(dialog.getVStage().getRoot().getContentPane(), update1FileChooseContent, -20);

                        content.getChildren().addAll(new VBox(
                                update1FileChooseContent,
                                update2FileChooseContent
                        ));

                        Optional<Integer> result = dialog.showAndWait();

                        if (result.filter(i -> i == ForkedDialog.CONFIRM).isPresent()) {
                            if (update1File == null && update2File == null) {
                                /* 不允许update1.rpf与update2.rpf同时为null */
                                ForkedAlert.showAndWait(Alert.AlertType.ERROR, fpI18n.illegalFontpackContribute);
                                return;
                            }

                            /* 字体包合法，询问用户字体包命名 */
                            Optional<String> fontpackName = readFontpackNameFromUser();
                            fontpackName.ifPresent(s -> {
                                /* 字体包名合法，开始导入字体包 */
                                if (update1File != null && update2File != null) {
                                    importNormalFontpack(s, update1File.toPath(), update2File.toPath());
                                } else {
                                    /* update.rpf不为空 */
                                    boolean flag = update1File != null;
                                    Path existUpdateFilePath = flag ? update1File.toPath() : update2File.toPath();
                                    int identity = flag ? 0 : 1;
                                    importNormalFontpack(s, existUpdateFilePath, identity);
                                }
                            });
                        }
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
                        ForkedDialog<Integer> dialog = ForkedDialog.simpleDialog(
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
            ForkedDialog.stackTraceDialog(ex, ForkedDialog.CONFIRM).showAndWait();
        }
    }

    private File selectDirectory(String title) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(title);
        return directoryChooser.showDialog(this.getContentPane().getScene().getWindow());
    }

    private boolean selectGameHome() {
        /* 让用户选择游戏目录 */
        File file = selectDirectory(fpI18n.chooseGameHome);
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

        /* 检查是否存在原始字体包 */
        Path originalUpdate1File = updateDir.resolve("update.rpf");  /* update.rpf */
        Path originalUpdate2File = updateDir.resolve("update2.rpf");  /* update2.rpf */

        if (Files.exists(originalUpdate1File) && Files.exists(originalUpdate2File)) {
            /* 存在原始字体包，询问是否保存 */
            ForkedDialog<Integer> dialog = ForkedDialog.simpleDialog(
                    MessageFormat.format(fpI18n.fontpackExisted,
                            originalUpdate1File.toAbsolutePath().toString(),
                            originalUpdate2File.toAbsolutePath().toString()),
                    Modality.APPLICATION_MODAL
            );

            Optional<Integer> result = dialog.showAndWait();
            if (result.filter(i -> i == ForkedDialog.CONFIRM).isPresent()) {
                /* 用户确认保存字体包，键入字体包名称 */
                Optional<String> fontpackName = readFontpackNameFromUser();
                fontpackName.ifPresent(s -> importBaseFontpack(s, originalUpdate1File, originalUpdate2File));
            }
        } else {
            /* 原始字体包不完整，提醒用户校验文件完整性 */
            ForkedAlert.showAndWait(Alert.AlertType.ERROR, fpI18n.illegalOriginalFontpackContribute);
            return false;
        }
        config.gameHome = absPath;
        gameHomeChooseBtn.setText(absPath);
        return true;
    }

    private Optional<String> readFontpackNameFromUser() {
        ForkedDialog<Integer> dialog = ForkedDialog.simpleDialog(Modality.APPLICATION_MODAL);
        dialog.getVStage().getStage().setWidth(600);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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

    /* 载入单个字体包文件，identity为0代表载入update.rpf，identity为1代表载入update2.rpf */
    private boolean overloadFontpack(FontpackMetadata fontpack, int identity) {
        String updateFilename = identity == 0 ? "update.rpf" : "update2.rpf";
        String patchFilename = identity == 0 ? "update2.rpf": "update1.rpf";

        Path updateFile = PathUtils.getFontpacksBaseDirPath().resolve(fontpack.getId()).resolve(updateFilename);

        List<FontpackMetadata> patchList = fontpackService.listFontPacksByCondition(FontpackMetadata.builder().isBased(true).build());

        Optional<FontpackMetadata> patchFileOption = patchList.stream().findFirst();

        if (patchFileOption.isEmpty()) return false;
        Path patchFile = PathUtils.getFontpacksBaseDirPath().resolve(patchFileOption.get().getId()).resolve(patchFilename);

        Path updateFileTarget = Path.of(config.gameHome).resolve("update").resolve(updateFilename);
        Path patchFileTarget = Path.of(config.gameHome).resolve("update").resolve(patchFilename);

        try {
//          Files.copy(source, updateFileTarget, StandardCopyOption.REPLACE_EXISTING);
            asyncCopyWithProgress(updateFile, updateFileTarget, new Callback<>() {
                @Override
                protected void onSucceeded(Void unused) {
                    /* 拷贝另一份文件 */
                    try {
                        asyncCopyWithProgress(patchFile, patchFileTarget, new Callback<>() {
                            @Override
                            protected void onSucceeded(Void unused) {
                                FXUtils.runDelay(20, () -> ForkedAlert.showAndWait(Alert.AlertType.INFORMATION, fpI18n.importSuccess));
                                /*拷贝成功 变更状态 */
                                updateAsEnableFontpack(fontpack);
                            }

                            @Override
                            protected void onFailed(LoadingFailure loadingFailure) {
                                FXUtils.runDelay(20, () ->
                                        ForkedAlert.showAndWait(Alert.AlertType.ERROR, loadingFailure.getMessage()));
                                ForkedDialog.stackTraceDialog(loadingFailure.getCause(), ForkedDialog.CONFIRM).showAndWait();
                            }
                        }, () -> {}, identity == 1 ? fpI18n.copyingUpdate1File : fpI18n.copyingUpdate2File);

                    } catch (IOException ex) {
                        /* 拷贝失败 */
                        ForkedDialog.stackTraceDialog(ex, ForkedDialog.CONFIRM).showAndWait();
                        Logger.error(LogType.FILE_ERROR, ex.getMessage());
                    }
                }

                @Override
                protected void onFailed(LoadingFailure loadingFailure) {
                    FXUtils.runDelay(20, () ->
                            ForkedAlert.showAndWait(Alert.AlertType.ERROR, loadingFailure.getMessage()));
                    ForkedDialog.stackTraceDialog(loadingFailure.getCause(), ForkedDialog.CONFIRM).showAndWait();
                }
            }, () -> {}, identity == 0 ? fpI18n.copyingUpdate1File : fpI18n.copyingUpdate2File);

        } catch (IOException ex) {
            /* 拷贝失败 */
            ForkedDialog.stackTraceDialog(ex, ForkedDialog.CONFIRM).showAndWait();
            Logger.error(LogType.FILE_ERROR, ex.getMessage());
        }

        return true;
    }

    /* 载入多文件字体包 */
    private boolean overloadFontpack(FontpackMetadata fontpack) {
        Path update1File = PathUtils.getFontpacksBaseDirPath().resolve(fontpack.getId()).resolve("update.rpf");
        Path update2File = PathUtils.getFontpacksBaseDirPath().resolve(fontpack.getId()).resolve("update2.rpf");

        if (Files.notExists(update1File) || Files.notExists(update2File)) return false;  /* 若文件其一不存在直接返回 */

        Path update1FileTarget = Path.of(config.gameHome).resolve("update").resolve("update.rpf");
        Path update2FileTarget = Path.of(config.gameHome).resolve("update").resolve("update2.rpf");

        try {
            /* 拷贝update.rpf */
            asyncCopyWithProgress(update1File, update1FileTarget, new Callback<>() {
                @Override
                protected void onSucceeded(Void unused) {
                    try {
                        /* 拷贝update.rpf */
                        asyncCopyWithProgress(update2File, update2FileTarget, new Callback<>() {
                            @Override
                            protected void onSucceeded(Void unused) {
                                FXUtils.runDelay(20, () -> ForkedAlert.showAndWait(Alert.AlertType.INFORMATION, fpI18n.importSuccess));
                                /*拷贝成功 变更状态 */
                                updateAsEnableFontpack(fontpack);
                            }

                            @Override
                            protected void onFailed(LoadingFailure loadingFailure) {
                                FXUtils.runDelay(20, () ->
                                        ForkedAlert.showAndWait(Alert.AlertType.ERROR, loadingFailure.getMessage()));
                                ForkedDialog.stackTraceDialog(loadingFailure.getCause(), ForkedDialog.CONFIRM).showAndWait();
                            }
                        }, () -> {}, fpI18n.copyingUpdate2File);

                    } catch (IOException ex) {
                        /* 拷贝失败 */
                        ForkedDialog.stackTraceDialog(ex, ForkedDialog.CONFIRM).showAndWait();
                        Logger.error(LogType.FILE_ERROR, ex.getMessage());
                    }
                }

                @Override
                protected void onFailed(LoadingFailure loadingFailure) {
                    FXUtils.runDelay(20, () ->
                            ForkedAlert.showAndWait(Alert.AlertType.ERROR, loadingFailure.getMessage()));
                    ForkedDialog.stackTraceDialog(loadingFailure.getCause(), ForkedDialog.CONFIRM).showAndWait();
                }
            }, () -> {}, fpI18n.copyingUpdate1File);

        } catch (IOException ex) {
            /* 拷贝失败 */
            ForkedDialog.stackTraceDialog(ex, ForkedDialog.CONFIRM).showAndWait();
            Logger.error(LogType.FILE_ERROR, ex.getMessage());
        }

        return true;
    }

    /**
     * 创建仅单个update文件的字体包
     * @param name 字体包名称
     * @param updateFile 字体包文件
     * @param identity 标志位，0代表仅上传update.rpf，1代表仅上传update2.rpf
     */
    private void importNormalFontpack(String name, Path updateFile, int identity) {
        try {
            long size = updateFile.toFile().length();
            FontpackMetadata newFontpack = fontpackService.createFontPack(name, false,"", 0, identity ,size, false);

            /* update.rpf非空，执行拷贝 */
            Path updateFileTarget = PathUtils.getFontpacksBaseDirPath().resolve(newFontpack.getId()).resolve(
                    identity == 0 ? "update.rpf" : "update2.rpf"
            );
            asyncCopyWithProgress(updateFile, updateFileTarget, new Callback<>() {
                @Override
                protected void onSucceeded(Void unused) {
                    /* 拷贝成功 */
                    FXUtils.runDelay(20, () -> ForkedAlert.showAndWait(Alert.AlertType.INFORMATION, fpI18n.importSuccess));
                    refreshTable();  /* 刷新表格 */
                }

                @Override
                protected void onFailed(LoadingFailure loadingFailure) {
                    _03FontPackScene.this.onFailed(loadingFailure, newFontpack.getId());
                }
            }, () -> {}, identity == 0 ? fpI18n.copyingUpdate1File : fpI18n.copyingUpdate2File);

        } catch (IOException e) {
            ForkedDialog.stackTraceDialog(e, ForkedDialog.CONFIRM).showAndWait();
            Logger.error(LogType.FILE_ERROR, e.getMessage());
        }
    }

    /**
     * 创建同时存在update.rpf，update2.rpf文件的字体包
     * @param name 字体包名称
     * @param update1File update.rpf字体包源文件路径
     * @param update2File update2.rpf字体包源文件路径
     */
    private void importNormalFontpack(String name, Path update1File, Path update2File) {
        if (update1File == null || update2File == null) return;

        try {
            long size = update1File.toFile().length() + update2File.toFile().length();
            FontpackMetadata newFontpack = fontpackService.createFontPack(name, false,"", 0, 2 ,size, false);
            Path update1FileTarget = PathUtils.getFontpacksBaseDirPath().resolve(newFontpack.getId()).resolve("update.rpf");  /* update.rpf目标资源路径 */
            Path update2FileTarget = PathUtils.getFontpacksBaseDirPath().resolve(newFontpack.getId()).resolve("update2.rpf");  /* update2.rpf目标资源路径 */

            /* update.rpf非空，执行拷贝 */
            asyncCopyWithProgress(update1File, update1FileTarget, new Callback<>() {
                @Override
                protected void onSucceeded(Void unused) {
                    /* 拷贝成功，继续拷贝update2.rpf */
                    try {
                        /* update2.rpf非空，执行拷贝 */
                        asyncCopyWithProgress(update2File, update2FileTarget, new Callback<>() {
                            @Override
                            protected void onSucceeded(Void unused) {
                                /* 拷贝成功 */
                                FXUtils.runDelay(20, () -> ForkedAlert.showAndWait(Alert.AlertType.INFORMATION, fpI18n.importSuccess));
                                refreshTable();
                            }

                            @Override
                            protected void onFailed(LoadingFailure loadingFailure) {
                                _03FontPackScene.this.onFailed(loadingFailure, newFontpack.getId());
                            }
                        }, () -> {}, fpI18n.copyingUpdate2File);

                    } catch (IOException e) {
                        ForkedDialog.stackTraceDialog(e, ForkedDialog.CONFIRM).showAndWait();
                        Logger.error(LogType.FILE_ERROR, e.getMessage());
                    }
                }

                @Override
                protected void onFailed(LoadingFailure loadingFailure) {
                    _03FontPackScene.this.onFailed(loadingFailure, newFontpack.getId());
                }
            }, () -> {}, fpI18n.copyingUpdate1File);

        } catch (IOException e) {
            ForkedDialog.stackTraceDialog(e, ForkedDialog.CONFIRM).showAndWait();
            Logger.error(LogType.FILE_ERROR, e.getMessage());
        }
    }

    /**
     * 拷贝基础字体包，基础字体包即gta根目录下默认存在的字体包(尽管无法确定这个字体包是否已经被用户修改，仍然将这个字体包作为'基'字体包，
     * 供填充其他不完整字体包)，通过自动识别update.rpf以及update2.rpf两个字体包文件，将gtav根目录下的字体包文件导入管理
     * @param name 字体包名称
     * @param originalUpdate1File update.rpf字体包源文件路径
     * @param originalUpdate2File update2.rpf字体包源文件路径
     */
    private void importBaseFontpack(String name, Path originalUpdate1File, Path originalUpdate2File) {
        try {
            long size1 = originalUpdate1File.toFile().length();
            long size2 = originalUpdate2File.toFile().length();
            long size = size1 + size2;

            FontpackMetadata newFontpack = fontpackService.createFontPack(name, true,"", 0, 2 ,size, true);  /* 将信息写入数据库 */
            Path update1FileTarget = PathUtils.getFontpacksBaseDirPath().resolve(newFontpack.getId()).resolve("update.rpf");  /* update.rpf目标资源路径 */
            Path update2FileTarget = PathUtils.getFontpacksBaseDirPath().resolve(newFontpack.getId()).resolve("update2.rpf");  /* update2.rpf目标资源路径 */
//            Files.copy(originalUpdate1File, update1FileTarget, StandardCopyOption.REPLACE_EXISTING);  /* 将文件拷贝到目标目录 */

            /* 拷贝update.rpf */
            asyncCopyWithProgress(originalUpdate1File, update1FileTarget, new Callback<>() {
                @Override
                protected void onSucceeded(Void unused) {
                    try {

                        /* 拷贝update2.rpf */
                        asyncCopyWithProgress(originalUpdate2File, update2FileTarget, new Callback<>() {
                            @Override
                            protected void onSucceeded(Void unused) {
                                /* 拷贝成功 */
                                FXUtils.runDelay(20, () -> ForkedAlert.showAndWait(Alert.AlertType.INFORMATION, fpI18n.importSuccess));
                                updateAsEnableFontpack(newFontpack);  /* 将基础字体包设置为激活状态 */
                                refreshTable();  /* 刷新表格 */
                            }

                            @Override
                            protected void onFailed(LoadingFailure loadingFailure) {
                                _03FontPackScene.this.onFailed(loadingFailure, newFontpack.getId());
                            }
                        }, () -> {}, fpI18n.copyingUpdate2File);

                    } catch (IOException e) {
                        ForkedDialog.stackTraceDialog(e, ForkedDialog.CONFIRM).showAndWait();
                        Logger.error(LogType.FILE_ERROR, e.getMessage());
                    }
                }

                @Override
                protected void onFailed(LoadingFailure loadingFailure) {
                    _03FontPackScene.this.onFailed(loadingFailure, newFontpack.getId());
                }
            }, () -> {}, fpI18n.copyingUpdate1File);

        } catch (IOException ex) {
            /* 创建字体包出错，回滚 */
            ForkedDialog.stackTraceDialog(ex, ForkedDialog.CONFIRM).showAndWait();
            Logger.error(LogType.FILE_ERROR, ex.getMessage());
        }
    }

    private void onFailed(LoadingFailure loadingFailure, String fontpackId) {
        FXUtils.runDelay(20, () -> ForkedDialog.stackTraceDialog(loadingFailure.getCause(), ForkedDialog.CONFIRM).showAndWait());
        try {
            fontpackService.deleteFontPack(fontpackId);  /* 清理文件 */
        } catch (IOException e) {
            ForkedDialog.stackTraceDialog(e, ForkedDialog.CONFIRM).showAndWait();
        }
    }

    private void asyncCopyWithProgress(Path source, Path target, Callback<Void, LoadingFailure> cb, Runnable doFinally, String intro) throws IOException {
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
            loadingItems.add(createLoadingItem(in, out, partSize, intro));
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
                    ForkedDialog.stackTraceDialog(ex, ForkedDialog.CONFIRM).showAndWait();
                    Logger.error(LogType.FILE_ERROR, ex.getMessage());
                } finally {
                    stage.close();
                    doFinally.run();
                }
            }
        });
    }

    /* 将每一次拷贝构建为一个分片任务 */
    private LoadingItem createLoadingItem(InputStream in, OutputStream out, int partSize, String intro) {
        return new LoadingItem(1, intro, () -> {
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

    private Optional<File> selectFontpackFile(String title) {
        /* 让用户选择字体包文件 */
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(fpI18n.fontpackFileDesc, "*.rpf")
        );
        return Optional.of(fileChooser.showOpenDialog(this.getContentPane().getScene().getWindow()));
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
