package club.pineclone.gtavops.client;

public interface UILifecycleAware {

    /**
     * UI 组件初始化时会被调用
     */
    default void onUIInit() {}

    /**
     * UI 组件销毁时被调用
     */
    default void onUIDispose() {}

}
