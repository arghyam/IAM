package com.arghyam.backend;

import com.arghyam.backend.config.AppContext;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableAutoConfiguration
public class SpringUserApplication {

    @Autowired
    AppContext appContext;

    public static void main(String[] args) {
        SpringApplication.run(SpringUserApplication.class, args);
    }

    @Bean
    public void setUpGraphDatabase() {
        Driver graphDBDriver = GraphDatabase.driver(appContext.getNeo4jBaseUri(), AuthTokens.basic(appContext.getNeo4jUsername(), appContext.getNeo4jPassword()));

    }
}

