Feature: Text Search Performance Testing
  As a performance engineer
  I want to measure the performance of text-based searches
  So that I can evaluate search functionality performance

  Background:
    Given the application is running
    And the databases are clean
    And 200 test documents are generated

  Scenario: Run text search performance test for 3 minutes
    When I run the text search performance test
    Then the test should complete successfully
    And MongoDB average response time should be less than 15ms
    And Elasticsearch average response time should be less than 20ms
    And the test should process at least 300 queries

  Scenario: Test text search with different terms
    When I search for text "rest"
    Then MongoDB response time should be less than 8ms
    And Elasticsearch response time should be less than 12ms
    And both databases should return the same results
    And the results should contain the search term

  Scenario: Test text search with partial matches
    When I search for text "antibio"
    Then MongoDB response time should be less than 8ms
    And Elasticsearch response time should be less than 12ms
    And both databases should return the same results
    And the results should contain recommendations with "antibiotics" 