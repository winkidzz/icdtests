package com.example.demo.repository.mongo;

import com.example.demo.model.MyDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoDocumentRepository extends MongoRepository<MyDocument, String> {
    // Spring Data MongoDB will implement these methods automatically
    List<MyDocument> findByDiagnosisRecommendationContaining(String searchTerm);
} 