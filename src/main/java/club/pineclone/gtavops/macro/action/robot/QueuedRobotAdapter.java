package club.pineclone.gtavops.macro.action.robot;

import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import io.vproxy.vfx.entity.input.Key;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 单消息队列机器人装饰器
 */
public class QueuedRobotAdapter extends VCRobotAdapter {

    private final VCRobotAdapter delegate;
    private final ExecutorService queue = Executors.newSingleThreadExecutor();

    public QueuedRobotAdapter(VCRobotAdapter delegate) {
        super(delegate.robot);
        this.delegate = delegate;
    }

    @Override
    public void simulate(Key key) throws Exception {
        queue.submit(() -> {
            try {
                delegate.simulate(key);
            } catch (Exception e) {
                Logger.error(LogType.SYS_ERROR, e.getMessage());
            }
        });
    }
}
