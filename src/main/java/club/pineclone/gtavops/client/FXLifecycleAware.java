package club.pineclone.gtavops.client;

public interface FXLifecycleAware {

    default void onFXStart() throws Exception {}

    default void onFXInit() throws Exception {}

    default void onFXStop() throws Exception {}

}
