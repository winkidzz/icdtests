Feature: Combined Query Performance Testing
  As a performance engineer
  I want to measure the performance of complex queries
  So that I can evaluate database performance for real-world scenarios

  Background:
    Given the application is running
    And the databases are clean
    And 200 test documents are generated

  Scenario: Run combined query performance test for 3 minutes
    When I run the combined query performance test
    Then the test should complete successfully
    And MongoDB average response time should be less than 10ms
    And Elasticsearch average response time should be less than 15ms
    And the test should process at least 500 queries

  Scenario: Test complex query with multiple criteria
    When I query with the following criteria:
      | patientAge | 25    |
      | ndcClass   | Antibiotics |
      | icdCode    | A01.1 |
    Then MongoDB response time should be less than 5ms
    And Elasticsearch response time should be less than 8ms
    And both databases should return the same results
    And the results should match the query criteria 