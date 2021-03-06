package org.forwater.backend;

import org.forwater.backend.config.AppContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableAutoConfiguration
@EnableCaching

public class SpringUserApplication {

    @Autowired
    AppContext appContext;

    public static void main(String[] args) {
        SpringApplication.run(SpringUserApplication.class, args);
    }
}

