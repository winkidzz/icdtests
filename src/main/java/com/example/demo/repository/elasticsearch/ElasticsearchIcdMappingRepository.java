package com.example.demo.repository.elasticsearch;

import com.example.demo.model.IcdCodeMapping;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ElasticsearchIcdMappingRepository extends ElasticsearchRepository<IcdCodeMapping, String> {
    @Query("{\"term\": {\"icdCodes\": \"?0\"}}")
    List<IcdCodeMapping> findByIcdCode(String icdCode);

    @Query("{\"match\": {\"diagnosisRecommendation\": \"?0\"}}")
    List<IcdCodeMapping> findByDiagnosisRecommendationContaining(String searchTerm);

    List<IcdCodeMapping> findByIcdCodesContaining(String icdCode);
    List<IcdCodeMapping> findByIcdCodesContainingAndPatientAgeBetweenAndNdcCodeClassAndNdcCodesContaining(
        String icdCode, int minAge, int maxAge, String ndcCodeClass, String ndcCode);
} 