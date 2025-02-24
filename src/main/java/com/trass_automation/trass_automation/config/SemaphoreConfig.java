package com.trass_automation.trass_automation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Semaphore;

@Configuration
public class SemaphoreConfig {

    @Bean
    public Semaphore SharedSemaphore() {
        return new Semaphore(1);
    }
}
