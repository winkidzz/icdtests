package com.example.demo.service;

import com.example.demo.model.IcdCodeMapping;
import com.example.demo.repository.mongo.MongoIcdMappingRepository;
import com.example.demo.repository.elasticsearch.ElasticsearchIcdMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerformanceComparisonService {

    private final MongoIcdMappingRepository mongoRepository;
    private final ElasticsearchIcdMappingRepository elasticsearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final MongoTemplate mongoTemplate;

    public IcdCodeMapping saveDocument(IcdCodeMapping mapping) {
        // Measure MongoDB save time
        long mongoStart = System.currentTimeMillis();
        mongoRepository.save(mapping);
        long mongoEnd = System.currentTimeMillis();
        long mongoTime = mongoEnd - mongoStart;

        // Measure Elasticsearch save time
        long esStart = System.currentTimeMillis();
        elasticsearchRepository.save(mapping);
        long esEnd = System.currentTimeMillis();
        long esTime = esEnd - esStart;

        log.info("Save times - MongoDB: {}ms, Elasticsearch: {}ms", mongoTime, esTime);
        return mapping;
    }

    public Optional<IcdCodeMapping> findById(String diagnosisId) {
        // Measure MongoDB findById time
        long mongoStart = System.currentTimeMillis();
        Optional<IcdCodeMapping> mongoResult = mongoRepository.findById(diagnosisId);
        long mongoEnd = System.currentTimeMillis();
        long mongoTime = mongoEnd - mongoStart;

        // Measure Elasticsearch findById time
        long esStart = System.currentTimeMillis();
        Optional<IcdCodeMapping> esResult = elasticsearchRepository.findById(diagnosisId);
        long esEnd = System.currentTimeMillis();
        long esTime = esEnd - esStart;

        log.info("FindById times - MongoDB: {}ms, Elasticsearch: {}ms", mongoTime, esTime);
        return mongoResult;
    }

    public List<IcdCodeMapping> searchText(String term) {
        return mongoRepository.findByDiagnosisRecommendationContaining(term);
    }

    public List<IcdCodeMapping> advancedSearch(
            String diagnosis,
            Integer minAge,
            Integer maxAge,
            String ndcCodeClass,
            String ndcCode) {
        
        // Create a query builder
        Query query = new Query();
        
        // Add diagnosis filter if provided
        if (diagnosis != null && !diagnosis.isEmpty()) {
            query.addCriteria(Criteria.where("diagnosisRecommendation")
                .regex(".*" + diagnosis + ".*", "i")); // Case-insensitive regex search
        }

        // Add age range filter if provided
        if (minAge != null || maxAge != null) {
            Criteria ageCriteria = new Criteria("patientAge");
            if (minAge != null && maxAge != null) {
                ageCriteria.gte(minAge).lte(maxAge);
            } else if (minAge != null) {
                ageCriteria.gte(minAge);
            } else {
                ageCriteria.lte(maxAge);
            }
            query.addCriteria(ageCriteria);
        }

        // Add NDC code class filter if provided
        if (ndcCodeClass != null && !ndcCodeClass.isEmpty()) {
            query.addCriteria(Criteria.where("ndcCodeClass")
                .regex(".*" + ndcCodeClass + ".*", "i")); // Case-insensitive regex search
        }

        // Add NDC code filter if provided
        if (ndcCode != null && !ndcCode.isEmpty()) {
            query.addCriteria(Criteria.where("ndcCodes")
                .regex(".*" + ndcCode + ".*", "i")); // Case-insensitive regex search
        }

        return mongoTemplate.find(query, IcdCodeMapping.class);
    }

    public void deleteById(String diagnosisId) {
        // Measure MongoDB delete time
        long mongoStart = System.currentTimeMillis();
        mongoRepository.deleteById(diagnosisId);
        long mongoEnd = System.currentTimeMillis();
        long mongoTime = mongoEnd - mongoStart;

        // Measure Elasticsearch delete time
        long esStart = System.currentTimeMillis();
        elasticsearchRepository.deleteById(diagnosisId);
        long esEnd = System.currentTimeMillis();
        long esTime = esEnd - esStart;

        log.info("Delete times - MongoDB: {}ms, Elasticsearch: {}ms", mongoTime, esTime);
    }

    public List<IcdCodeMapping> findByIcdCodesContainingAndPatientAgeBetweenAndNdcCodeClassAndNdcCodesContaining(
            String icdCode,
            int minAge,
            int maxAge,
            String ndcCodeClass,
            String ndcCode) {
        
        org.springframework.data.mongodb.core.query.Query query = 
            new org.springframework.data.mongodb.core.query.Query();
        
        // Add ICD code filter
        query.addCriteria(
            org.springframework.data.mongodb.core.query.Criteria.where("icdCodes")
                .regex(".*" + icdCode + ".*", "i")); // Case-insensitive regex search

        // Add age range filter
        query.addCriteria(
            org.springframework.data.mongodb.core.query.Criteria.where("patientAge")
                .gte(minAge)
                .lte(maxAge));

        // Add NDC code class filter
        if (ndcCodeClass != null && !ndcCodeClass.isEmpty()) {
            query.addCriteria(
                org.springframework.data.mongodb.core.query.Criteria.where("ndcCodeClass")
                    .is(ndcCodeClass));
        }

        // Add NDC code filter
        if (ndcCode != null && !ndcCode.isEmpty()) {
            query.addCriteria(
                org.springframework.data.mongodb.core.query.Criteria.where("ndcCodes")
                    .regex(".*" + ndcCode + ".*", "i")); // Case-insensitive regex search
        }

        return mongoTemplate.find(query, IcdCodeMapping.class);
    }
} 