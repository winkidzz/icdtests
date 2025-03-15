package com.example.demo.service;

import com.example.demo.config.TestConfig;
import com.example.demo.model.MyDocument;
import com.example.demo.repository.mongo.MongoDocumentRepository;
import com.example.demo.repository.elasticsearch.ElasticsearchDocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestConfig.class)
@ActiveProfiles("test")
public class PerformanceTestServiceTest {

    @Autowired
    private PerformanceTestService testService;

    @Autowired
    private MongoDocumentRepository mongoRepository;

    @Autowired
    private ElasticsearchDocumentRepository elasticsearchRepository;

    @BeforeEach
    void setUp() {
        mongoRepository.deleteAll();
        elasticsearchRepository.deleteAll();
    }

    @Test
    void runIdBasedTest_ShouldCompleteSuccessfully() {
        // When
        String report = testService.runIdBasedTest();

        // Then
        assertNotNull(report);
        assertTrue(report.contains("ID-based Performance Test Results"));
        assertTrue(report.contains("MongoDB Performance"));
        assertTrue(report.contains("Elasticsearch Performance"));
    }

    @Test
    void generateTestData_ShouldCreateDocumentsInBothDatabases() {
        // When
        testService.generateTestData(10);

        // Then
        long mongoCount = mongoRepository.count();
        long elasticsearchCount = elasticsearchRepository.count();

        assertEquals(10, mongoCount);
        assertEquals(10, elasticsearchCount);
    }

    @Test
    void cleanDatabases_ShouldRemoveAllDocuments() {
        // Given
        MyDocument doc = new MyDocument();
        doc.setIcdCode("A01.0");
        doc.setDiagnosisRecommendation("Test recommendation");
        doc.setCreatedAt(LocalDateTime.now());
        doc.setLastUpdated(LocalDateTime.now());
        
        mongoRepository.save(doc);
        elasticsearchRepository.save(doc);

        // When
        testService.cleanDatabases();

        // Then
        assertEquals(0, mongoRepository.count());
        assertEquals(0, elasticsearchRepository.count());
    }

    @Test
    void generateTestReport_ShouldFormatResultsCorrectly() {
        // Given
        List<Long> mongoTimes = List.of(50L, 60L, 70L);
        List<Long> elasticsearchTimes = List.of(30L, 40L, 50L);

        // When
        String report = testService.generateTestReport(mongoTimes, elasticsearchTimes);

        // Then
        assertNotNull(report);
        assertTrue(report.contains("ID-based Performance Test Results"));
        assertTrue(report.contains("MongoDB Performance"));
        assertTrue(report.contains("Elasticsearch Performance"));
    }
} 