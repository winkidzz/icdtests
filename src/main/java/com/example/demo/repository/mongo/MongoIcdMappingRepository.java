package com.example.demo.repository.mongo;

import com.example.demo.model.IcdCodeMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MongoIcdMappingRepository extends MongoRepository<IcdCodeMapping, String> {
    @Query("{ 'icdCodes': ?0 }")
    List<IcdCodeMapping> findByIcdCode(String icdCode);

    @Query("{ 'diagnosisRecommendation': { $regex: ?0, $options: 'i' } }")
    List<IcdCodeMapping> findByDiagnosisRecommendationContaining(String searchTerm);

    List<IcdCodeMapping> findByIcdCodesContaining(String icdCode);
    List<IcdCodeMapping> findByIcdCodesContainingAndPatientAgeBetweenAndNdcCodeClassAndNdcCodesContaining(
        String icdCode, int minAge, int maxAge, String ndcCodeClass, String ndcCode);
} 