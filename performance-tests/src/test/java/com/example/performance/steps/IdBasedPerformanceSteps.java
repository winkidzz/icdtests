package com.example.performance.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class IdBasedPerformanceSteps {
    private Response response;

    @When("I run the ID-based performance test for {int} minutes")
    public void runIdBasedTest(int minutes) {
        response = RestAssured.post("/api/performance/test/id-based");
        assert response.getStatusCode() == HttpStatus.OK.value();
    }

    @Then("I should see the ID-based query performance metrics")
    public void verifyIdBasedMetrics() {
        String responseBody = response.getBody().asString();
        assert responseBody.contains("MongoDB Performance");
        assert responseBody.contains("Elasticsearch Performance");
    }

    @When("I query for ICD codes by ID")
    public void queryIcdCodesById() {
        response = RestAssured.get("/api/icd-codes/1");
        assert response.getStatusCode() == HttpStatus.OK.value();
    }

    @Then("I should get valid responses from both databases")
    public void verifyResponses() {
        String responseBody = response.getBody().asString();
        assert responseBody != null && !responseBody.isEmpty();
    }
} 