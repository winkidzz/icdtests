package com.example.demo.repository.elasticsearch;

import com.example.demo.model.MyDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticsearchDocumentRepository extends ElasticsearchRepository<MyDocument, String> {
    // Spring Data Elasticsearch will implement these methods automatically
} 