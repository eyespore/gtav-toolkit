package club.pineclone.gtavops.macro.trigger;

/* 信号源 */
/**
 * @see KeySource
 */
public abstract class InputSource {

    protected InputSourceListener listener;

    public void setListener(InputSourceListener listener) {
        this.listener = listener;
    }

    public abstract void install();

    public abstract void uninstall();
}
