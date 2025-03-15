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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestConfig.class)
@ActiveProfiles("test")
public class PerformanceComparisonServiceTest {

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
    void testMongoDbFindById() {
        // Given
        MyDocument doc = new MyDocument();
        doc.setIcdCode("A01.0");
        doc.setDiagnosisRecommendation("Test recommendation");
        doc.setCreatedAt(LocalDateTime.now());
        doc.setLastUpdated(LocalDateTime.now());
        mongoRepository.save(doc);

        // When
        long startTime = System.currentTimeMillis();
        Optional<MyDocument> result = mongoRepository.findById(doc.getIcdCode());
        long endTime = System.currentTimeMillis();

        // Then
        assertTrue(result.isPresent());
        assertTrue((endTime - startTime) < 100, "MongoDB find by ID took more than 100ms");
    }

    @Test
    void testElasticsearchFindById() {
        // Given
        MyDocument doc = new MyDocument();
        doc.setIcdCode("A01.1");
        doc.setDiagnosisRecommendation("Test recommendation");
        doc.setCreatedAt(LocalDateTime.now());
        doc.setLastUpdated(LocalDateTime.now());
        elasticsearchRepository.save(doc);

        // When
        long startTime = System.currentTimeMillis();
        Optional<MyDocument> result = elasticsearchRepository.findById(doc.getIcdCode());
        long endTime = System.currentTimeMillis();

        // Then
        assertTrue(result.isPresent());
        assertTrue((endTime - startTime) < 100, "Elasticsearch find by ID took more than 100ms");
    }

    @Test
    void testMongoDbSave() {
        // Given
        MyDocument doc = new MyDocument();
        doc.setIcdCode("A01.2");
        doc.setDiagnosisRecommendation("Test recommendation");
        doc.setCreatedAt(LocalDateTime.now());
        doc.setLastUpdated(LocalDateTime.now());

        // When
        long startTime = System.currentTimeMillis();
        MyDocument savedDoc = mongoRepository.save(doc);
        long endTime = System.currentTimeMillis();

        // Then
        assertNotNull(savedDoc);
        assertTrue((endTime - startTime) < 100, "MongoDB save took more than 100ms");
    }

    @Test
    void testElasticsearchSave() {
        // Given
        MyDocument doc = new MyDocument();
        doc.setIcdCode("A01.3");
        doc.setDiagnosisRecommendation("Test recommendation");
        doc.setCreatedAt(LocalDateTime.now());
        doc.setLastUpdated(LocalDateTime.now());

        // When
        long startTime = System.currentTimeMillis();
        MyDocument savedDoc = elasticsearchRepository.save(doc);
        long endTime = System.currentTimeMillis();

        // Then
        assertNotNull(savedDoc);
        assertTrue((endTime - startTime) < 100, "Elasticsearch save took more than 100ms");
    }
} 