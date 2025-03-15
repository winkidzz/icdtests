package com.example.demo.service;

import com.example.demo.model.IcdCodeMapping;
import com.example.demo.repository.mongo.MongoIcdMappingRepository;
import com.example.demo.repository.elasticsearch.ElasticsearchIcdMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
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

    public List<IcdCodeMapping> searchText(String searchTerm) {
        // Measure MongoDB search time
        long mongoStart = System.currentTimeMillis();
        List<IcdCodeMapping> mongoResults = mongoRepository.findByDiagnosisRecommendationContaining(searchTerm);
        long mongoEnd = System.currentTimeMillis();
        long mongoTime = mongoEnd - mongoStart;

        // Measure Elasticsearch search time
        long esStart = System.currentTimeMillis();
        Criteria criteria = new Criteria("diagnosisRecommendation").contains(searchTerm);
        CriteriaQuery query = new CriteriaQuery(criteria);
        SearchHits<IcdCodeMapping> searchHits = elasticsearchOperations.search(query, IcdCodeMapping.class);
        List<IcdCodeMapping> esResults = searchHits.stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
        long esEnd = System.currentTimeMillis();
        long esTime = esEnd - esStart;

        log.info("Search times - MongoDB: {}ms, Elasticsearch: {}ms", mongoTime, esTime);
        return esResults;
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
} 