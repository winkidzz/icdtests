package com.example.demo.service;

import com.example.demo.model.MyDocument;
import com.example.demo.repository.mongo.MongoDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final MongoDocumentRepository documentRepository;

    @Cacheable(value = "documents", key = "#id", unless = "#result == null")
    public Optional<MyDocument> findById(String id) {
        log.info("Fetching document from MongoDB with id: {}", id);
        return documentRepository.findById(id);
    }

    public List<MyDocument> searchByRecommendation(String searchTerm) {
        log.info("Searching documents by recommendation containing: {}", searchTerm);
        return documentRepository.findByDiagnosisRecommendationContaining(searchTerm);
    }

    @CacheEvict(value = "documents", key = "#document.icdCode")
    public MyDocument save(MyDocument document) {
        LocalDateTime now = LocalDateTime.now();
        if (document.getCreatedAt() == null) {
            document.setCreatedAt(now);
        }
        document.setLastUpdated(now);
        log.info("Saving document to MongoDB with ICD code: {}", document.getIcdCode());
        return documentRepository.save(document);
    }

    @CacheEvict(value = "documents", key = "#id")
    public void deleteById(String id) {
        log.info("Deleting document from MongoDB with id: {}", id);
        documentRepository.deleteById(id);
    }
} 