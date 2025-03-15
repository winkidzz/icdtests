package com.example.performance.config;

import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import jakarta.annotation.PostConstruct;

@Configuration
@PropertySource("classpath:application.properties")
public class TestConfig {

    @Value("${api.base.url:http://localhost:8080}")
    private String baseUrl;

    @PostConstruct
    public void init() {
        RestAssured.baseURI = baseUrl;
    }
} 