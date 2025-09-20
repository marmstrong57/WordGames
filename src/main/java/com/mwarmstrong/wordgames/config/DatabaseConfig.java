package com.mwarmstrong.wordgames.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;

import javax.sql.DataSource;

@Configuration
@Slf4j
public class DatabaseConfig {

    @Bean
    @Profile("!test")
    public DataSource dataSource(AppSettings appSettings) {
        String path = appSettings.getCachePath();
        String url = StringUtils.isAllEmpty(path)
                ? "jdbc:h2:mem:words" : "jdbc:h2:file:" + path + "/words";
        url = url.replace('\\', '/');
        return createDataSource(url);
    }

    @Bean(name = "dataSource")
    @Profile("test")
    public DataSource testDataSource() {
        return createDataSource("jdbc:h2:mem:words");
    }


    DataSource createDataSource(String url) {
        log.info("Creating H2 DataSource with URL: {}", url);
        return DataSourceBuilder.create()
                .driverClassName("org.h2.Driver")
                .url(url)
                .username("sa")
                .password("")
                .build();
    }

}
