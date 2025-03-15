package com.example.performance.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Component
public class TextSearchPerformanceSteps {

    private Response response;
    private List<String> searchResults;

    @When("I run the text search performance test for {int} minutes")
    public void runTextSearchTest(int minutes) {
        response = RestAssured.post("/api/performance/test/text-search");
        assert response.getStatusCode() == HttpStatus.OK.value();
    }

    @Then("I should see the text search performance metrics")
    public void verifyTextSearchMetrics() {
        String responseBody = response.getBody().asString();
        assert responseBody.contains("MongoDB Performance");
        assert responseBody.contains("Elasticsearch Performance");
    }

    @When("I search for the term {string}")
    public void searchForTerm(String term) {
        response = RestAssured.get("/api/icd-codes/search?term=" + term);
        assert response.getStatusCode() == HttpStatus.OK.value();
    }

    @Then("I should get valid search results")
    public void verifySearchResults() {
        String responseBody = response.getBody().asString();
        assert responseBody != null && !responseBody.isEmpty();
    }

    @When("I search with partial term {string}")
    public void searchWithPartialTerm(String term) {
        response = RestAssured.get("/api/icd-codes/search?term=" + term);
        assert response.getStatusCode() == HttpStatus.OK.value();
    }

    @Then("MongoDB average response time should be less than {int}ms")
    public void mongoDBAverageResponseTimeShouldBeLessThan(int maxTime) {
        // TODO: Implement MongoDB response time validation
    }

    @Then("Elasticsearch average response time should be less than {int}ms")
    public void elasticsearchAverageResponseTimeShouldBeLessThan(int maxTime) {
        // TODO: Implement Elasticsearch response time validation
    }

    @Then("the test should process at least {int} queries")
    public void testShouldProcessAtLeastQueries(int minQueries) {
        // TODO: Implement query count validation
    }

    @Then("both databases should return the same results")
    public void bothDatabasesShouldReturnTheSameResults() {
        // TODO: Implement result comparison
    }

    @Then("the results should contain the search term")
    public void resultsShouldContainSearchTerm() {
        assert searchResults.stream()
            .anyMatch(result -> result.toLowerCase().contains("rest"));
    }

    @Then("the results should contain recommendations with {string}")
    public void resultsShouldContainRecommendationsWith(String term) {
        assert searchResults.stream()
            .anyMatch(result -> result.toLowerCase().contains(term.toLowerCase()));
    }
} 