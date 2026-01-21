package club.pineclone.toolkit.config;

import club.pineclone.toolkit.core.macro.MacroContext;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class MacroConfiguration {

    /* 宏任务上下文 */
    @Bean
    public MacroContext macroContext() {
        MacroContext context = MacroContext.getInstance();
        context.init();  // 直接在 Bean 创建时初始化
        return context;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startMacroContext() {
        MacroContext.getInstance().start();
    }

    @PreDestroy
    public void stopMacroContext() {
        MacroContext.getInstance().stop();
    }

}
