package com.mwarmstrong.wordgames.config;

import com.mwarmstrong.wordgames.utilities.AppUtils;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "wordgames")
@Slf4j
public class AppSettings {
    private String cachePath;

    @PostConstruct
    public void logIt() {
        log.info(AppUtils.toJson(this));
    }
}
