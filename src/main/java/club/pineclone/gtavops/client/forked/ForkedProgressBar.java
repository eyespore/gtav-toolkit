package club.pineclone.gtavops.client.forked;

import io.vproxy.base.util.callback.Callback;
import io.vproxy.vfx.manager.internal_i18n.InternalI18n;
import io.vproxy.vfx.manager.task.TaskManager;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.loading.LoadingFailure;
import io.vproxy.vfx.ui.loading.LoadingItem;
import io.vproxy.vfx.ui.shapes.VLine;
import io.vproxy.vfx.util.FXUtils;
import io.vproxy.vfx.util.MiscUtils;
import javafx.application.Platform;
import javafx.beans.property.DoublePropertyBase;
import javafx.scene.Group;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class ForkedProgressBar extends Group {

    private static final double radius = 1;
    private static final double width = radius * 2;

    @Getter private double length;
    @Getter private double progress;
    private final VLine backgroundLine = new VLine(width);
    private final VLine progressLine = new VLine(width);

    public ForkedProgressBar() {
        getChildren().addAll(backgroundLine, progressLine);
        backgroundLine.setStartX(radius);
        backgroundLine.setStroke(Theme.current().progressBarBackgroundColor());

        progressLine.setStartX(radius);
        progressLine.setStroke(Theme.current().progressBarProgressColor());
    }

    public void setLength(double length) {
        this.length = length;
        backgroundLine.setEndX(length - radius);
        updateProgressLine();
    }

    @Getter private final DoublePropertyBase progressProperty = new DoublePropertyBase() {
        @Override
        protected void invalidated() {
            setProgress(progressProperty.get());
        }

        @Override
        public Object getBean() {
            return ForkedProgressBar.this;
        }

        @Override
        public String getName() {
            return "progressProperty";
        }
    };

    public void setProgress(double progress) {
        if (progress < 0) {
            progress = 0;
        } else if (progress > 1) {
            progress = 1;
        }
        this.progress = progress;
        progressProperty.set(progress);
        updateProgressLine();
    }

    private void updateProgressLine() {
        var l = length * progress;
        progressLine.setEndX(l - radius);
    }

    @Setter private List<LoadingItem> items = new ArrayList<>();
    @Setter long interval = 0;
    @Setter private Callback<Void, LoadingFailure> cb = null;
    private volatile boolean isDone = false;
    @Setter  private Consumer<LoadingItem> currentLoadingItemCallback = null;

    public void load(Callback<Void, LoadingFailure> cb) {
        this.cb = cb;

        long total = 0;
        for (var item : items) {
            total += item.weight;
        }
        loadItem(total, 0, items.iterator(), () -> Platform.runLater(() -> {
            isDone = true;
            Platform.runLater(() -> Platform.runLater(cb::succeeded));
        }));
    }

    private void loadItem(long total, long current, Iterator<LoadingItem> ite, Runnable cb) {
        if (!ite.hasNext()) {
            cb.run();
            return;
        }
        if (isDone) {
            return;
        }
        var item = ite.next();
        FXUtils.runOnFX(() -> {
            var currentCB = currentLoadingItemCallback;
            if (currentCB != null) {
                currentCB.accept(item);
            }
        });
        TaskManager.get().execute(() -> {
            final boolean ok;
            final Throwable loadingException;
            {
                boolean _ok;
                Throwable _ex;
                try {
                    _ok = item.loadFunc.getAsBoolean();
                    _ex = null;
                } catch (Throwable t) {
                    _ex = t;
                    _ok = false;
                }
                ok = _ok;
                loadingException = _ex;
            }
            if (ok) {
                if (interval > 0) {
                    MiscUtils.threadSleep(interval);
                }
            }
            Platform.runLater(() -> {
                if (!ok) {
                    isDone = true;
                    Platform.runLater(() -> this.cb.failed(new LoadingFailure(item, loadingException)));
                    return;
                }
                long newCurr = current + item.weight;
                setProgress(newCurr / (double) total);
                loadItem(total, newCurr, ite, cb);
            });
        });
    }

    public boolean terminate() {
        if (isDone) {
            return false;
        }
        isDone = true;
        FXUtils.runOnFX(() -> cb.failed(new LoadingFailure(InternalI18n.get().loadingCanceled())));
        return true;
    }

}
