package com.mwarmstrong.wordgames.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(AppSettings.class)
@ComponentScan("com.mwarmstrong.wordgames")
@Slf4j
public class AppConfig {

    @PostConstruct
    public void logIt() {
        log.info("Loaded {}", this.getClass().getSimpleName());
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
