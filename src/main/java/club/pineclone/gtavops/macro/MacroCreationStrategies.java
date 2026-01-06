package club.pineclone.gtavops.macro;

import club.pineclone.gtavops.common.SessionType;
import club.pineclone.gtavops.config.MacroConfig;
import club.pineclone.gtavops.config.MacroConfigLoader;
import club.pineclone.gtavops.macro.action.Action;
import club.pineclone.gtavops.macro.action.impl.swapglitch.SwapGlitchAction;
import club.pineclone.gtavops.macro.action.impl.swapglitch.SwapMeleeAction;
import club.pineclone.gtavops.macro.action.impl.swapglitch.SwapRangedAction;
import club.pineclone.gtavops.macro.action.impl.*;
import club.pineclone.gtavops.macro.action.impl.betterlbutton.HoldLButtonAction;
import club.pineclone.gtavops.macro.action.impl.betterlbutton.RapidlyClickLButtonAction;
import club.pineclone.gtavops.macro.action.impl.betterlbutton.RemapLButtonAction;
import club.pineclone.gtavops.macro.action.impl.bettermmenu.AutoSnakeAction;
import club.pineclone.gtavops.macro.action.impl.bettermmenu.StartEngineAction;
import club.pineclone.gtavops.macro.action.impl.betterpmenu.JoinABookmarkedJobAction;
import club.pineclone.gtavops.macro.action.impl.betterpmenu.JoinANewSessionAction;
import club.pineclone.gtavops.macro.trigger.Trigger;
import club.pineclone.gtavops.macro.trigger.TriggerFactory;
import club.pineclone.gtavops.macro.trigger.TriggerIdentity;
import club.pineclone.gtavops.macro.trigger.TriggerMode;
import io.vproxy.vfx.entity.input.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MacroCreationStrategies {

    private static final Logger log = LoggerFactory.getLogger(MacroCreationStrategies.class);

    /* 切枪偷速宏 */
    // TODO: 将宏策略移交到独立的工具类MacroCreationStrategies当中，考虑是否抽离Dto
    public static final MacroCreationStrategy SWAP_GLITCH_MACRO_CREATION_STRATEGY = new MacroCreationStrategy() {
        @Override
        public Macro apply(MacroConfig config) {
            MacroConfig.SwapGlitch.BaseSetting baseSetting = config.swapGlitch.baseSetting;
            MacroConfig.SwapGlitch.SwapRangedSetting swapRangedSetting = config.swapGlitch.swapRangedSetting;
            MacroConfig.SwapGlitch.SwapMeleeSetting swapMeleeSetting = config.swapGlitch.swapMeleeSetting;

            Key activatekey = baseSetting.activatekey;  /* 激活热键 */
            TriggerMode mode = baseSetting.activateMethod;  /* 激活模式 切换执行 or 按住执行 */
            TriggerIdentity defaultIdentity = TriggerIdentity.of(mode, activatekey);  /* 触发器 */

            Trigger trigger = TriggerFactory.simple(defaultIdentity);

            Key weaponWheelHotkey = baseSetting.targetWeaponWheelKey;  /* 武器轮盘热键 */
            long interval = (long) Math.floor(baseSetting.triggerInterval);  /* 偷速间隔 */

            Action action = new SwapGlitchAction(weaponWheelHotkey, interval);  /* 基础执行器 */

            boolean swapMelee = swapMeleeSetting.enableSwapMelee;  /* 切入偷速前是否切换近战 */
            if (swapMelee) {  /* 套装饰器 */
                Key swapMeleeHotkey = swapMeleeSetting.meleeWeaponKey;
                long postSwapMeleeDelay = (long) Math.floor(swapMeleeSetting.postSwapMeleeDelay);  /* 偷速间隔 */
                action = new SwapMeleeAction(action, swapMeleeHotkey, postSwapMeleeDelay);
            }

            boolean swapRanged = swapRangedSetting.enableSwapRanged;  /* 切出偷速前是否切换远程 */
            if (swapRanged) {
                Map<Key, Key> sourceToTargetMap = new HashMap<>();

                /* 启用映射1 */
                if (swapRangedSetting.enableMapping1)
                    sourceToTargetMap.put(swapRangedSetting.mapping1SourceKey, swapRangedSetting.mapping1TargetKey);
                /* 启用映射2 */
                if (swapRangedSetting.enableMapping2)
                    sourceToTargetMap.put(swapRangedSetting.mapping2SourceKey, swapRangedSetting.mapping2TargetKey);
                /* 启用映射3 */
                if (swapRangedSetting.enableMapping3)
                    sourceToTargetMap.put(swapRangedSetting.mapping3SourceKey, swapRangedSetting.mapping3TargetKey);
                /* 启用映射4 */
                if (swapRangedSetting.enableMapping4)
                    sourceToTargetMap.put(swapRangedSetting.mapping4SourceKey, swapRangedSetting.mapping4TargetKey);
                /* 启用映射5 */
                if (swapRangedSetting.enableMapping5)
                    sourceToTargetMap.put(swapRangedSetting.mapping5SourceKey, swapRangedSetting.mapping5TargetKey);

                /* 空值映射 */
                if (swapRangedSetting.enableClearKey)
                    sourceToTargetMap.put(swapRangedSetting.clearKey, null);

                action = new SwapRangedAction(action,
                        swapRangedSetting.defaultRangedWeaponKey,
                        swapRangedSetting.swapDefaultRangedWeaponOnEmpty,
                        sourceToTargetMap);

                if (!sourceToTargetMap.isEmpty()) {
                    /* 映射表不为空，基于子动作实现武器切换 */
                    log.debug("Register union trigger for swap glitch macro");
                    TriggerIdentity recordIdentify = TriggerIdentity.of(TriggerMode.CLICK, sourceToTargetMap.keySet());
                    trigger = TriggerFactory.union(recordIdentify, defaultIdentity);
                }
            }
            return createSimpleMacro(trigger, action);
        }
    };

    /* 轮盘零食宏 */
    public static final MacroCreationStrategy ROULETTE_SNAKE_MACRO_CREATION_STRATEGY = new MacroCreationStrategy() {
        @Override
        public Macro apply(MacroConfig config) {
            MacroConfig.RouletteSnake rsConfig = config.rouletteSnake;

            Key snakeKey = rsConfig.baseSetting.snakeKey;
            Key activatekey = rsConfig.baseSetting.activatekey;
            Key weaponWheelKey = rsConfig.baseSetting.weaponWheel;
            long triggerInterval = (long) Math.floor(rsConfig.baseSetting.triggerInterval);

            TriggerIdentity identity1 = TriggerIdentity.of(TriggerMode.HOLD, activatekey);
            TriggerIdentity identity2 = TriggerIdentity.of(TriggerMode.HOLD, weaponWheelKey);
            Trigger trigger = TriggerFactory.composite(identity1, identity2);

            Action action = new RouletteSnakeAction(triggerInterval, snakeKey);
            return createSimpleMacro(trigger, action);
        }
    };

    /* AD 摇宏 */
    public static final MacroCreationStrategy AD_SWING_MACRO_CREATION_STRATEGY = new MacroCreationStrategy() {
        @Override
        public Macro apply(MacroConfig config) {
            MacroConfig.ADSwing adwConfig = config.adSwing;

            TriggerMode mode = adwConfig.baseSetting.activateMethod;  /* 激活模式 */
            Key activatekey = adwConfig.baseSetting.activatekey;  /* 激活热键 */

            Trigger trigger;
            if (adwConfig.baseSetting.enableSafetyKey) {  /* 启用保险键 */
                Key safetyKey = adwConfig.baseSetting.safetyKey;
                trigger = TriggerFactory.composite(
                        TriggerIdentity.of(mode, activatekey),
                        TriggerIdentity.of(mode, safetyKey)
                );
            } else {
                trigger = TriggerFactory.simple(TriggerIdentity.of(mode, activatekey));  /* 触发器 */
            }

            long triggerInterval = (long) Math.floor(adwConfig.baseSetting.triggerInterval);
            Key moveLeftKey = adwConfig.baseSetting.moveLeftKey;
            Key moveRightKey = adwConfig.baseSetting.moveRightKey;
            Action action = new ADSwingAction(triggerInterval, moveLeftKey, moveRightKey);

            return createSimpleMacro(trigger, action);
        }
    };

    /* 近战偷速宏 */
    public static final MacroCreationStrategy MELEE_GLITCH_MACRO_CREATION_STRATEGY = new MacroCreationStrategy() {
        @Override
        public Macro apply(MacroConfig config) {
            MacroConfig.MeleeGlitch mgConfig = config.meleeGlitch;

            TriggerMode mode = mgConfig.baseSetting.activateMethod;  /* 激活模式 切换执行 or 按住执行 */
            Key activatekey = mgConfig.baseSetting.activatekey;  /* 激活热键 */

            Trigger trigger;
            if (mgConfig.baseSetting.enableSafetyKey) {
                Key safetyKey = mgConfig.baseSetting.safetyKey;
                trigger = TriggerFactory.composite(
                        TriggerIdentity.of(mode, activatekey),
                        TriggerIdentity.of(mode, safetyKey)
                );
            } else {
                trigger = TriggerFactory.simple(TriggerIdentity.of(mode, activatekey));  /* 触发器 */

            }

            long triggerInterval = (long) Math.floor(mgConfig.baseSetting.triggerInterval);
            Key meleeSnakeScrollKey = mgConfig.baseSetting.meleeSnakeScrollKey;
            Action action = new MeleeGlitchAction(triggerInterval, meleeSnakeScrollKey);

            return createSimpleMacro(trigger, action);
        }
    };

    /* 更好 M 菜单宏 */
    /* 快速点火宏 */
    public static final MacroCreationStrategy START_ENGINE_MACRO_CREATION_STRATEGY = new MacroCreationStrategy() {
        @Override
        public Macro apply(MacroConfig config) {
            MacroConfig.BetterMMenu bmmConfig = config.betterMMenu;

            long mouseScrollInterval = (long) (Math.floor(bmmConfig.baseSetting.mouseScrollInterval));
            long keyPressInterval = (long) (Math.floor(bmmConfig.baseSetting.keyPressInterval));
            long timeUtilMMenuLoaded = (long) (Math.floor(bmmConfig.baseSetting.timeUtilMMenuLoaded));
            Key menuKey = bmmConfig.baseSetting.menuKey;

            Key activateKey = bmmConfig.startEngine.activateKey;
            boolean enableDoubleClickToOpenDoor = bmmConfig.startEngine.enableDoubleClickToOpenDoor;
            long doubleClickInterval = (long) (Math.floor(bmmConfig.startEngine.doubleClickInterval));

            Trigger trigger;
            if (enableDoubleClickToOpenDoor) trigger = TriggerFactory.simple(TriggerIdentity.ofDoubleClick(doubleClickInterval, activateKey));  // 启用双击触发
            else trigger = TriggerFactory.simple(TriggerIdentity.ofClick(activateKey));  // 仅启用单击触发

            Action action = new StartEngineAction(menuKey, mouseScrollInterval, keyPressInterval, timeUtilMMenuLoaded, enableDoubleClickToOpenDoor);

            return createSimpleMacro(trigger, action);
        }
    };

    /* 自动 M 菜单零食宏 */
    public static final MacroCreationStrategy AUTO_SNAKE_MACRO_CREATION_STRATEGY = new MacroCreationStrategy() {
        @Override
        public Macro apply(MacroConfig config) {
            MacroConfig.BetterMMenu bmmConfig = config.betterMMenu;

            long mouseScrollInterval = (long) (Math.floor(bmmConfig.baseSetting.mouseScrollInterval));
            long keyPressInterval = (long) (Math.floor(bmmConfig.baseSetting.keyPressInterval));
            long timeUtilMMenuLoaded = (long) (Math.floor(bmmConfig.baseSetting.timeUtilMMenuLoaded));
            Key menuKey = bmmConfig.baseSetting.menuKey;

            Key activateKey = bmmConfig.autoSnake.activateKey;
            boolean refillVest = bmmConfig.autoSnake.refillVest;
            boolean keepMMenu = bmmConfig.autoSnake.keepMMenu;

            Trigger trigger = TriggerFactory.simple(TriggerIdentity.ofHold(activateKey));
            Action action = new AutoSnakeAction(
                    menuKey, mouseScrollInterval, keyPressInterval, timeUtilMMenuLoaded, refillVest, keepMMenu);

            return createSimpleMacro(trigger, action);
        }
    };

    /* 更好左键宏 */
    /* 辅助按住鼠标左键宏 */
    public static final MacroCreationStrategy HOLD_LEFT_BUTTON_MACRO_CREATION_STRATEGY = new MacroCreationStrategy() {
        @Override
        public Macro apply(MacroConfig config) {
            MacroConfig.BetterLButton blbConfig = config.betterLButton;

            TriggerMode mode = blbConfig.holdLButtonSetting.activateMethod;
            Key activateKey = blbConfig.holdLButtonSetting.activateKey;
            Trigger trigger = TriggerFactory.simple(TriggerIdentity.of(mode, activateKey));

            Action action = new HoldLButtonAction();

            return createSimpleMacro(trigger, action);
        }
    };

    /* 辅助点按鼠标左键宏 */
    public static final MacroCreationStrategy RAPIDLY_CLICK_LEFT_BUTTON_MACRO_CREATION_STRATEGY = new MacroCreationStrategy() {
        @Override
        public Macro apply(MacroConfig config) {
            MacroConfig.BetterLButton blbConfig = config.betterLButton;

            TriggerMode mode = blbConfig.rapidlyClickLButtonSetting.activateMethod;
            Key activateKey = blbConfig.rapidlyClickLButtonSetting.activateKey;
            long triggerInterval = (long) (Math.floor(blbConfig.rapidlyClickLButtonSetting.triggerInterval));

            Trigger trigger = TriggerFactory.simple(TriggerIdentity.of(mode, activateKey));
            Action action = new RapidlyClickLButtonAction(triggerInterval);

            return createSimpleMacro(trigger, action);
        }
    };

    /* 鼠标左键重映射宏 */
    public static final MacroCreationStrategy REMAP_LEFT_BUTTON_MACRO_CREATION_STRATEGY = new MacroCreationStrategy() {
        @Override
        public Macro apply(MacroConfig config) {
            MacroConfig.BetterLButton blbConfig = config.betterLButton;

            Key activateKey = blbConfig.remapLButtonSetting.activateKey;
            Action action = new RemapLButtonAction();
            Trigger trigger = TriggerFactory.simple(TriggerIdentity.of(TriggerMode.HOLD, activateKey));

            return createSimpleMacro(trigger, action);
        }
    };

    /* 切枪自动确认宏 */
    public static final MacroCreationStrategy QUICK_SWAP_MACRO_CREATION_STRATEGY = new MacroCreationStrategy() {
        @Override
        public Macro apply(MacroConfig config) {
            MacroConfig.SwapGlitch sgConfig = config.swapGlitch;
            MacroConfig.QuickSwap qsConfig = config.quickSwap;

            Map<Key, Key> sourceToTargetMap = new HashMap<>();
            MacroConfig.SwapGlitch.SwapRangedSetting srSetting = sgConfig.swapRangedSetting;
            MacroConfig.QuickSwap.BaseSetting bSetting = qsConfig.baseSetting;

            /* 启用映射1 */
            if (bSetting.enableMapping1) sourceToTargetMap.put(srSetting.mapping1SourceKey, srSetting.mapping1TargetKey);
            /* 启用映射2 */
            if (bSetting.enableMapping2) sourceToTargetMap.put(srSetting.mapping2SourceKey, srSetting.mapping2TargetKey);
            /* 启用映射3 */
            if (bSetting.enableMapping3) sourceToTargetMap.put(srSetting.mapping3SourceKey, srSetting.mapping3TargetKey);
            /* 启用映射4 */
            if (bSetting.enableMapping4) sourceToTargetMap.put(srSetting.mapping4SourceKey, srSetting.mapping4TargetKey);
            /* 启用映射5 */
            if (bSetting.enableMapping5) sourceToTargetMap.put(srSetting.mapping5SourceKey, srSetting.mapping5TargetKey);

            if (sourceToTargetMap.isEmpty()) return null;

            Action action = new QuickSwapAction(
                    sourceToTargetMap,
                    bSetting.blockKey,
                    (long) Math.floor(bSetting.blockDuration));

            Trigger trigger;
            TriggerIdentity defaultIdentify = TriggerIdentity.of(TriggerMode.CLICK, sourceToTargetMap.keySet());

            if (!bSetting.enableBlockKey) {
                /* 未启用屏蔽键 */
                trigger = TriggerFactory.simple(defaultIdentify);
            } else {
                /* 启用屏蔽键 */
                TriggerIdentity blockIdentify = TriggerIdentity.of(TriggerMode.HOLD, bSetting.blockKey);
                trigger = TriggerFactory.union(defaultIdentify, blockIdentify);
            }

            return createSimpleMacro(trigger, action);
        }
    };

    /* 延迟攀宏 */
    public static final MacroCreationStrategy DELAY_CLIMB_MACRO_CREATION_STRATEGY = new MacroCreationStrategy() {
        @Override
        public Macro apply(MacroConfig config) {
            MacroConfig.DelayClimb dcconfig = MacroConfigLoader.get().delayClimb;

            Key toggleDelayClimbKey = dcconfig.baseSetting.toggleDelayClimbKey;
            Key usePhoneKey = dcconfig.baseSetting.usePhoneKey;
            Key hideInCoverKey = dcconfig.baseSetting.hideInCoverKey;

            /* 自由活动的时间间隔 */
            long triggerInterval = (long) (Math.floor(dcconfig.baseSetting.triggerInterval));

            /* 等待相机退出的时间间隔 */
            long timeUtilCameraExited = (long) (Math.floor(dcconfig.baseSetting.timeUtilCameraExited));
            long timeUtilCameraLoaded1 = (long) (Math.floor(dcconfig.baseSetting.timeUtilCameraLoaded1));
            long timeUtilCameraLoaded2 = (long) (Math.floor(dcconfig.baseSetting.timeUtilCameraLoaded2));

            /* 是否在退出时切入掩体 */
            boolean hideInCoverOnExit = dcconfig.baseSetting.hideInCoverOnExit;

            Trigger trigger = TriggerFactory.simple(TriggerIdentity.of(TriggerMode.CLICK, toggleDelayClimbKey));
            Action action = new DelayClimbAction(
                    usePhoneKey, hideInCoverKey, triggerInterval,
                    timeUtilCameraExited, timeUtilCameraLoaded1, timeUtilCameraLoaded2, hideInCoverOnExit);

            return createSimpleMacro(trigger, action);
        }
    };

    /* 更好 P 菜单宏 */
    /* 加入新战局宏 */
    public static final MacroCreationStrategy JOIN_A_NEW_SESSION_MACRO_CREATION_STRATEGY = new MacroCreationStrategy() {
        @Override
        public Macro apply(MacroConfig config) {
            MacroConfig.BetterPMenu bpmconfig = MacroConfigLoader.get().betterPMenu;

            long mouseScrollInterval = (long) (Math.floor(bpmconfig.baseSetting.mouseScrollInterval));
            long enterKeyInterval = (long) (Math.floor(bpmconfig.baseSetting.enterKeyInterval));
            long timeUtilPMenuLoaded = (long) (Math.floor(bpmconfig.baseSetting.timeUtilPMenuLoaded));

            Key activateKey = bpmconfig.getJoinANewSession().activateKey;
            SessionType sessionType = bpmconfig.joinANewSession.sessionType;

            Trigger trigger = TriggerFactory.simple(TriggerIdentity.of(TriggerMode.CLICK, activateKey));
            Action action = new JoinANewSessionAction(sessionType, mouseScrollInterval, enterKeyInterval, timeUtilPMenuLoaded);

            return createSimpleMacro(trigger, action);
        }
    };

    /* 加入已收藏差事宏 */
    public static final MacroCreationStrategy JOIN_A_BOOKMARKED_JOB_MACRO_CREATION_STRATEGY = new MacroCreationStrategy() {
        @Override
        public Macro apply(MacroConfig config) {
            MacroConfig.BetterPMenu bpmconfig = MacroConfigLoader.get().betterPMenu;

            long mouseScrollInterval = (long) (Math.floor(bpmconfig.baseSetting.mouseScrollInterval));
            long enterKeyInterval = (long) (Math.floor(bpmconfig.baseSetting.enterKeyInterval));
            long timeUtilPMenuLoaded = (long) (Math.floor(bpmconfig.baseSetting.timeUtilPMenuLoaded));

            Key activateKey = bpmconfig.getJoinABookmarkedJob().activateKey;
            long timeUtilJobsLoaded = (long) (Math.floor(bpmconfig.joinABookmarkedJob.timeUtilJobsLoaded));

            Trigger trigger = TriggerFactory.simple(TriggerIdentity.of(TriggerMode.CLICK, activateKey));
            Action action = new JoinABookmarkedJobAction(mouseScrollInterval, enterKeyInterval, timeUtilPMenuLoaded, timeUtilJobsLoaded);

            return createSimpleMacro(trigger, action);
        }
    };

    public static final MacroCreationStrategy AUTO_FIRE_MACRO_CREATION_STRATEGY = new MacroCreationStrategy() {
        @Override
        public Macro apply(MacroConfig config) {
            MacroConfig.AutoFire.BaseSetting baseSetting = config.autoFire.getBaseSetting();

            Key activateKey = baseSetting.activateKey;
            TriggerMode activateMethod = baseSetting.activateMethod;
            Key heavyWeaponKey = baseSetting.heavyWeaponKey;
            Key specialWeaponKey = baseSetting.specialWeaponKey;
            long triggerInterval = (long) (Math.floor(baseSetting.triggerInterval));
            long mousePressInterval = (long) (Math.floor(baseSetting.mousePressInterval));

            Trigger trigger = TriggerFactory.simple(TriggerIdentity.of(activateMethod, activateKey));
//            Action action = new AutoFireAction(heavyWeaponKey, specialWeaponKey, triggerInterval, mousePressInterval);

//            return createSimpleMacro(trigger, action);
            return null;
        }
    };

    /**
     * 宏创建策略
     */
    public static abstract class MacroCreationStrategy implements Function<MacroConfig, Macro> {
        protected Macro createSimpleMacro(Trigger trigger, Action action) {
            SimpleMacro macro = new SimpleMacro(trigger, action);
            /* 创建宏时，如果全局处于挂起状态，那么需要将新生的宏挂起 */
            if (MacroRegistry.isGlobalSuspended()) macro.suspend();
            return macro;
        }
    }
}
