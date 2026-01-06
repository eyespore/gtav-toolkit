package club.pineclone.gtavops.client.forked;

import io.vproxy.vfx.animation.AnimationGraph;
import io.vproxy.vfx.animation.AnimationGraphBuilder;
import io.vproxy.vfx.animation.AnimationNode;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.util.FXUtils;
import io.vproxy.vfx.util.algebradata.DoubleData;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import lombok.Getter;

import java.util.function.Function;

public class ForkedFusionW extends StackPane {

    public final Node node;
    public final StringProperty property;
    @Getter private final Label label = new Label() {{
        setTextFill(Theme.current().normalTextColor());
    }};
    private final AnimationNode<DoubleData> showLabel = new AnimationNode<>("label", new DoubleData(0));
    private final AnimationNode<DoubleData> showNode = new AnimationNode<>("node", new DoubleData(1));
    private final AnimationGraph<DoubleData> animation;

    public ForkedFusionW(Labeled node) {
        this(node, Labeled::textProperty);
    }

    public ForkedFusionW(TextInputControl node) {
        this(node, TextInputControl::textProperty);
    }

    public ForkedFusionW(ComboBox<?> node) {
        this(node, n -> {
            var p = new SimpleStringProperty();
            n.setOnMouseExited(e -> node.commitValue());
            n.valueProperty().addListener((ob, old, now) -> updateComboBox(p, node));
            n.converterProperty().addListener((ob, old, now) -> updateComboBox(p, node));
            updateComboBox(p, node);
            return p;
        });
    }

    private static void updateComboBox(SimpleStringProperty p, @SuppressWarnings("rawtypes") ComboBox node) {
        var converter = node.getConverter();
        if (converter == null) {
            p.set(toStringOrEmpty(node.getValue()));
        } else {
            //noinspection unchecked
            p.set(converter.toString(node.getValue()));
        }
    }

    private static String toStringOrEmpty(Object o) {
        if (o == null) return "";
        return o.toString();
    }

    public <T extends Node> ForkedFusionW(T node, Function<T, StringProperty> fluentPropertyGetter) {
        this.node = node;
        this.property = fluentPropertyGetter.apply(node);
        FXUtils.disableFocusColor(this.node);

        node.layoutBoundsProperty().addListener((ob, old, now) -> update());
        update();
        property.addListener((ob, old, now) -> {
            if (now == null) now = "";
            label.setText(now);
        });
        label.textProperty().addListener((ob, old, now) -> update());

        animation = AnimationGraphBuilder
                .simpleTwoNodeGraph(showLabel, showNode, 300)
                .setApply((from, to, d) -> {
                    node.setOpacity(d.value);
                    label.setOpacity(1 - d.value);
                })
                .build(showLabel);

        getChildren().add(label);
        getChildren().add(node);

        setOnMouseEntered(e -> animation.play(showNode));
        setOnMouseExited(e -> animation.play(showLabel));

        label.setText(property.get());
    }

    private void update() {
        var bounds = node.getLayoutBounds();
        if (bounds.getWidth() > 0) {
            setPrefWidth(bounds.getWidth());
            label.setPrefWidth(bounds.getWidth());
        }
        if (bounds.getHeight() > 0) {
            setPrefHeight(bounds.getHeight());
            label.setPrefHeight(bounds.getHeight());
        }
    }

    public void enableLabelBackground() {
        label.setBackground(new Background(new BackgroundFill(
                Theme.current().fusionWrapperBackgroundColor(),
                new CornerRadii(4),
                Insets.EMPTY
        )));
    }

}
