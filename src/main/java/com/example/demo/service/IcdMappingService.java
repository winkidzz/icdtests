package com.example.demo.service;

import com.example.demo.model.IcdCodeMapping;
import com.example.demo.repository.mongo.MongoIcdMappingRepository;
import com.example.demo.repository.elasticsearch.ElasticsearchIcdMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class IcdMappingService {
    private final MongoIcdMappingRepository mongoRepository;
    private final ElasticsearchIcdMappingRepository elasticsearchRepository;

    public IcdCodeMapping save(IcdCodeMapping mapping) {
        log.info("Saving ICD mapping with ID: {}", mapping.getDiagnosisId());
        // Save to both databases
        mongoRepository.save(mapping);
        return elasticsearchRepository.save(mapping);
    }

    public Optional<IcdCodeMapping> findById(String diagnosisId) {
        log.info("Finding ICD mapping by diagnosis ID: {}", diagnosisId);
        return mongoRepository.findById(diagnosisId);
    }

    public List<IcdCodeMapping> findByIcdCode(String icdCode) {
        log.info("Finding diagnosis by ICD code: {}", icdCode);
        return mongoRepository.findByIcdCode(icdCode);
    }

    public List<IcdCodeMapping> searchByRecommendation(String searchTerm) {
        log.info("Searching diagnoses by recommendation containing: {}", searchTerm);
        return elasticsearchRepository.findByDiagnosisRecommendationContaining(searchTerm);
    }

    public void addIcdCodesToMapping(String diagnosisId, Set<String> newIcdCodes) {
        Optional<IcdCodeMapping> existingMapping = findById(diagnosisId);
        if (existingMapping.isPresent()) {
            IcdCodeMapping mapping = existingMapping.get();
            mapping.getIcdCodes().addAll(newIcdCodes);
            save(mapping);
        }
    }

    public void removeIcdCodesFromMapping(String diagnosisId, Set<String> icdCodesToRemove) {
        Optional<IcdCodeMapping> existingMapping = findById(diagnosisId);
        if (existingMapping.isPresent()) {
            IcdCodeMapping mapping = existingMapping.get();
            mapping.getIcdCodes().removeAll(icdCodesToRemove);
            save(mapping);
        }
    }

    public void deleteById(String diagnosisId) {
        log.info("Deleting ICD mapping with ID: {}", diagnosisId);
        mongoRepository.deleteById(diagnosisId);
        elasticsearchRepository.deleteById(diagnosisId);
    }
} 