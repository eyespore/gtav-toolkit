package club.pineclone.toolkit.domain.dto.macro;

import club.pineclone.toolkit.core.macro.input.Key;

/**
 * 延迟攀宏创建数据模型
 * @param toggleDelayClimbKey 延迟攀宏功能开关键
 * @param usePhoneKey 使用手机按键
 * @param hideInCoverKey 躲入掩体键
 * @param triggerInterval 开关手机触发间隔，也就是玩家在两次打开手机时间能够自由走动的时间
 * @param timeUtilCameraExited 等待相机退出的时间间隔
 * @param timeUtilCameraLoaded1 第一次等待相机加载的时间间隔，由于第一次加载初始化资源较多，因此和后续的手机加载时间区分开来
 * @param timeUtilCameraLoaded2 第二次手机加载等待时长
 * @param hideInCoverOnExit 在结束时是否自动躲入掩体，可以卡住延迟攀爬的状态
 */
public record DelayClimbDTO(
        Key toggleDelayClimbKey,
        Key usePhoneKey,
        Key hideInCoverKey,
        PercentageDTO triggerInterval,
        PercentageDTO timeUtilCameraExited,
        PercentageDTO timeUtilCameraLoaded1,
        PercentageDTO timeUtilCameraLoaded2,
        Boolean hideInCoverOnExit
) {
}
