package com.example.demo;

import com.example.demo.model.IcdCodeMapping;
import com.example.demo.service.PerformanceComparisonService;
import com.example.demo.repository.mongo.MongoIcdMappingRepository;
import com.example.demo.repository.elasticsearch.ElasticsearchIcdMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class PerformanceComparisonServiceTest {

    @Autowired
    private PerformanceComparisonService service;

    @Autowired
    private MongoIcdMappingRepository mongoRepository;

    @Autowired
    private ElasticsearchIcdMappingRepository elasticsearchRepository;

    @BeforeEach
    void setUp() {
        // Clean up both databases before each test
        mongoRepository.deleteAll();
        elasticsearchRepository.deleteAll();
    }

    @Test
    public void testCreateAndSearchDocument() {
        // Create a test document
        IcdCodeMapping mapping = new IcdCodeMapping();
        mapping.setDiagnosisRecommendation("Test Diagnosis");
        mapping.setPatientAge(30);
        mapping.setNdcCodeClass("Test Class");
        mapping.setNdcCodes(Set.of("TEST123"));
        mapping.setIcdCodes(Set.of("A00.0"));

        // Save the document
        IcdCodeMapping savedMapping = service.saveDocument(mapping);
        assertNotNull(savedMapping.getId());

        // Search for the document
        List<IcdCodeMapping> searchResults = service.searchText("Test Diagnosis");
        assertFalse(searchResults.isEmpty(), "Search results should not be empty after saving");
        assertEquals("Test Diagnosis", searchResults.get(0).getDiagnosisRecommendation());

        // Advanced search
        List<IcdCodeMapping> advancedResults = service.advancedSearch(
            "Test Diagnosis",
            25,
            35,
            "Test Class",
            "TEST123"
        );
        assertFalse(advancedResults.isEmpty(), "Advanced search results should not be empty");
        assertEquals("Test Diagnosis", advancedResults.get(0).getDiagnosisRecommendation());
    }
} 