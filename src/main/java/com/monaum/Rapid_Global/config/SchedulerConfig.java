package com.monaum.Rapid_Global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Zubayer Ahamed
 * 
 * @since Jul 3, 2025
 */
@Configuration
public class SchedulerConfig {

	@Bean
	ThreadPoolTaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(50); // tune based on load
		scheduler.setThreadNamePrefix("task-reminder-");
		scheduler.setRemoveOnCancelPolicy(true);
		scheduler.initialize();
		return scheduler;
	}
}
