Feature: ID-based Performance Testing
  As a performance engineer
  I want to measure the performance of ID-based queries
  So that I can compare MongoDB and Elasticsearch performance

  Background:
    Given the application is running
    And the databases are clean
    And 200 test documents are generated

  Scenario: Run ID-based performance test for 3 minutes
    When I run the ID-based performance test
    Then the test should complete successfully
    And MongoDB average response time should be less than 5ms
    And Elasticsearch average response time should be less than 10ms
    And the test should process at least 1000 queries

  Scenario: Compare individual query performance
    When I query for ICD code "A01.1"
    Then MongoDB response time should be less than 3ms
    And Elasticsearch response time should be less than 5ms
    And both databases should return the same results 