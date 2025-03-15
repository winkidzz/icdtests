package com.example.performance.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Component
public class CommonSteps {
    private Response response;

    @Before
    public void setup() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Given("the application is running")
    public void checkApplicationIsRunning() {
        response = RestAssured.get("/actuator/health");
        assert response.getStatusCode() == HttpStatus.OK.value();
    }

    @Given("the databases are clean")
    public void cleanDatabases() {
        response = RestAssured.post("/api/performance/test/clean");
        assert response.getStatusCode() == HttpStatus.OK.value();
    }

    @Given("there are {int} test documents")
    public void generateTestDocuments(int count) {
        response = RestAssured.post("/api/performance/test/generate?count=" + count);
        assert response.getStatusCode() == HttpStatus.OK.value();
    }
} 