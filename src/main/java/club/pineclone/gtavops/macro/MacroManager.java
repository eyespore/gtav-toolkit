package club.pineclone.gtavops.macro;

import club.pineclone.gtavops.macro.trigger.Trigger;
import lombok.Data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Deprecated
public class MacroManager {


    private final Map<String, PriorityQueue<PriorityMacro>> runningMap = new HashMap<>();  /* 运行队列 */
    private final Map<String, List<PriorityMacro>> suspendMap = new HashMap<>();  /* 挂起队列 */

    /* 注册优先级宏，替代原先的install方法 */
    public synchronized void register(PriorityMacro macro) {
        String mutexGroup = macro.getMutexGroup();  /* 检查是否存在互斥组，如果存在那么推迟注册 */

        if (mutexGroup == null) {
            /* 无互斥组，直接运行即可 */
            macro.install();
            return;
        }

        /* 获取当前互斥组 */
        PriorityQueue<PriorityMacro> queue = runningMap
                .computeIfAbsent(mutexGroup, k -> new PriorityQueue<>(
                        Comparator.comparingInt(PriorityMacro::getPriority)));

        if (queue.isEmpty()) {  /* 当前互斥组为空 */
            macro.install();
            queue.add(macro);  /* 将宏加入运行队列 */
        } else {  /* 互斥组不为空 */
            PriorityMacro peek = queue.peek();  /* 窥视优先级最高者 */
            if (peek.getPriority() < macro.getPriority()) {
                /* 优先级高于最高者，将最高者挂起 */
                peek.suspend();
                suspendMap.computeIfAbsent(mutexGroup, k -> new ArrayList<>()).add(macro);  /* 将其加入等待区 */

                macro.install();
                queue.poll();  /* 移除久宏 */
                queue.add(macro);  /* 添加新宏 */
            } else if (peek.getPriority() > macro.getPriority()) {
                /* 新宏优先级较低，需要先挂起 */
                macro.suspend();
                suspendMap.computeIfAbsent(mutexGroup, k -> new ArrayList<>()).add(macro);
            }
        }
    }

    /* 注销宏，替代原先的uninstall方法 */
    public void unregisterMacro(PriorityMacro macro) {

    }

}
