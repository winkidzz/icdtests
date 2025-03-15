package com.example.demo.service;

import com.example.demo.config.TestConfig;
import com.example.demo.model.IcdCodeMapping;
import com.example.demo.repository.mongo.MongoIcdMappingRepository;
import com.example.demo.repository.elasticsearch.ElasticsearchIcdMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestConfig.class)
@ActiveProfiles("test")
public class IcdMappingServiceTest {

    @Autowired
    private IcdMappingService mappingService;

    @Autowired
    private MongoIcdMappingRepository mongoRepository;

    @Autowired
    private ElasticsearchIcdMappingRepository elasticsearchRepository;

    @BeforeEach
    void setUp() {
        mongoRepository.deleteAll();
        elasticsearchRepository.deleteAll();
    }

    @Test
    void testSaveAndFindById() {
        // Given
        IcdCodeMapping mapping = new IcdCodeMapping();
        mapping.setDiagnosisId("D1");
        mapping.setDiagnosisRecommendation("Test recommendation");
        Set<String> icdCodes = new HashSet<>();
        icdCodes.add("A01.0");
        icdCodes.add("A01.1");
        mapping.setIcdCodes(icdCodes);

        // When
        IcdCodeMapping savedMapping = mappingService.save(mapping);
        Optional<IcdCodeMapping> found = mappingService.findById(savedMapping.getDiagnosisId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(2, found.get().getIcdCodes().size());
        assertTrue(found.get().getIcdCodes().contains("A01.0"));
        assertTrue(found.get().getIcdCodes().contains("A01.1"));
    }

    @Test
    void testFindByIcdCode() {
        // Given
        IcdCodeMapping mapping = new IcdCodeMapping();
        mapping.setDiagnosisId("D1");
        mapping.setDiagnosisRecommendation("Test recommendation");
        Set<String> icdCodes = new HashSet<>();
        icdCodes.add("B01.0");
        icdCodes.add("B01.1");
        mapping.setIcdCodes(icdCodes);
        mappingService.save(mapping);

        // When
        List<IcdCodeMapping> foundMappings = mappingService.findByIcdCode("B01.0");

        // Then
        assertFalse(foundMappings.isEmpty());
        assertTrue(foundMappings.get(0).getIcdCodes().contains("B01.0"));
    }

    @Test
    void testSearchByRecommendation() {
        // Given
        IcdCodeMapping mapping = new IcdCodeMapping();
        mapping.setDiagnosisId("D1");
        mapping.setDiagnosisRecommendation("Unique test recommendation");
        Set<String> icdCodes = new HashSet<>();
        icdCodes.add("C01.0");
        mapping.setIcdCodes(icdCodes);
        mappingService.save(mapping);

        // When
        List<IcdCodeMapping> foundMappings = mappingService.searchByRecommendation("unique test");

        // Then
        assertFalse(foundMappings.isEmpty());
        assertEquals("Unique test recommendation", foundMappings.get(0).getDiagnosisRecommendation());
    }

    @Test
    void testAddAndRemoveIcdCodes() {
        // Given
        IcdCodeMapping mapping = new IcdCodeMapping();
        mapping.setDiagnosisId("D1");
        mapping.setDiagnosisRecommendation("Test recommendation");
        Set<String> initialCodes = new HashSet<>();
        initialCodes.add("D01.0");
        mapping.setIcdCodes(initialCodes);
        mappingService.save(mapping);

        // When adding new codes
        Set<String> newCodes = new HashSet<>();
        newCodes.add("D01.1");
        newCodes.add("D01.2");
        mappingService.addIcdCodesToMapping("D1", newCodes);

        // Then
        Optional<IcdCodeMapping> afterAdd = mappingService.findById("D1");
        assertTrue(afterAdd.isPresent());
        assertEquals(3, afterAdd.get().getIcdCodes().size());

        // When removing codes
        Set<String> codesToRemove = new HashSet<>();
        codesToRemove.add("D01.1");
        mappingService.removeIcdCodesFromMapping("D1", codesToRemove);

        // Then
        Optional<IcdCodeMapping> afterRemove = mappingService.findById("D1");
        assertTrue(afterRemove.isPresent());
        assertEquals(2, afterRemove.get().getIcdCodes().size());
        assertFalse(afterRemove.get().getIcdCodes().contains("D01.1"));
    }

    @Test
    void testDeleteById() {
        // Given
        IcdCodeMapping mapping = new IcdCodeMapping();
        mapping.setDiagnosisId("D1");
        mapping.setDiagnosisRecommendation("Test recommendation");
        Set<String> icdCodes = new HashSet<>();
        icdCodes.add("E01.0");
        mapping.setIcdCodes(icdCodes);
        mappingService.save(mapping);

        // When
        mappingService.deleteById("D1");

        // Then
        Optional<IcdCodeMapping> found = mappingService.findById("D1");
        assertFalse(found.isPresent());
    }
} 