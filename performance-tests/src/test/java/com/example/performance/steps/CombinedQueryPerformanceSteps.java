package com.example.performance.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class CombinedQueryPerformanceSteps {
    private Response response;

    @When("I run the combined query performance test for {int} minutes")
    public void runCombinedTest(int minutes) {
        response = RestAssured.post("/api/performance/test/combined");
        assert response.getStatusCode() == HttpStatus.OK.value();
    }

    @Then("I should see the combined query performance metrics")
    public void verifyCombinedMetrics() {
        String responseBody = response.getBody().asString();
        assert responseBody.contains("MongoDB Performance");
        assert responseBody.contains("Elasticsearch Performance");
    }

    @When("I query with multiple criteria")
    public void queryWithMultipleCriteria() {
        response = RestAssured.get("/api/icd-codes/search?diagnosis=diabetes&patientAge=50&ndcCode=12345");
        assert response.getStatusCode() == HttpStatus.OK.value();
    }

    @Then("I should get valid combined search results")
    public void verifyCombinedResults() {
        String responseBody = response.getBody().asString();
        assert responseBody != null && !responseBody.isEmpty();
    }
} 