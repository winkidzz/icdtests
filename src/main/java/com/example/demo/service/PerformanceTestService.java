package com.example.demo.service;

import com.example.demo.model.IcdCodeMapping;
import com.example.demo.repository.mongo.MongoIcdMappingRepository;
import com.example.demo.repository.elasticsearch.ElasticsearchIcdMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerformanceTestService {

    private static final Duration TEST_DURATION = Duration.ofMinutes(3);
    private static final String[][] ICD_CODE_GROUPS = {
        {"A01.0", "A01.1"}, // Group 1
        {"B01.0", "B01.1", "B01.2"}, // Group 2
        {"C01.0", "C01.1", "C01.2", "C01.3"}, // Group 3
        {"D01.0", "D01.1", "D01.2", "D01.3", "D01.4"} // Group 4
    };
    private static final String[] RECOMMENDATIONS = {
        "Rest and hydration recommended for condition group 1",
        "Antibiotics prescribed for 7 days for condition group 2",
        "Regular monitoring required for condition group 3",
        "Immediate specialist consultation needed for condition group 4"
    };

    private static final String[] NDC_CODE_CLASSES = {
        "Antibiotics", "Analgesics", "Antihypertensives", "Antidiabetics", "Antidepressants"
    };

    private static final String[][] NDC_CODES = {
        {"0002-3225-01", "0002-3225-02", "0002-3225-03"}, // Antibiotics
        {"0002-3226-01", "0002-3226-02", "0002-3226-03"}, // Analgesics
        {"0002-3227-01", "0002-3227-02", "0002-3227-03"}, // Antihypertensives
        {"0002-3228-01", "0002-3228-02", "0002-3228-03"}, // Antidiabetics
        {"0002-3229-01", "0002-3229-02", "0002-3229-03"}  // Antidepressants
    };

    private final MongoIcdMappingRepository mongoRepository;
    private final ElasticsearchIcdMappingRepository elasticsearchRepository;
    private final Random random = new Random();

    public String runIdBasedTest() {
        log.info("Starting ID-based performance test for {} duration", TEST_DURATION);
        List<Long> mongoTimes = new ArrayList<>();
        List<Long> elasticsearchTimes = new ArrayList<>();

        LocalDateTime endTime = LocalDateTime.now().plus(TEST_DURATION);

        while (LocalDateTime.now().isBefore(endTime)) {
            String randomIcdCode = getRandomIcdCode();
            
            // Test MongoDB
            long mongoStart = System.currentTimeMillis();
            List<IcdCodeMapping> mongoResult = mongoRepository.findByIcdCode(randomIcdCode);
            long mongoEnd = System.currentTimeMillis();
            mongoTimes.add(mongoEnd - mongoStart);

            // Test Elasticsearch
            long esStart = System.currentTimeMillis();
            List<IcdCodeMapping> esResult = elasticsearchRepository.findByIcdCode(randomIcdCode);
            long esEnd = System.currentTimeMillis();
            elasticsearchTimes.add(esEnd - esStart);

            log.info("ID: {}, MongoDB: {}ms, Elasticsearch: {}ms", 
                    randomIcdCode, mongoEnd - mongoStart, esEnd - esStart);
        }

        return generateTestReport(mongoTimes, elasticsearchTimes);
    }

    public void generateTestData(int count) {
        log.info("Generating {} test documents...", count);
        cleanDatabases();

        for (int i = 0; i < count; i++) {
            IcdCodeMapping mapping = new IcdCodeMapping();
            mapping.setId("D" + (i + 1));
            mapping.setDiagnosisId("D" + (i + 1));
            
            // Generate random number of ICD codes (1-3)
            int numIcdCodes = random.nextInt(3) + 1;
            Set<String> icdCodes = new HashSet<>();
            for (int j = 0; j < numIcdCodes; j++) {
                String[] group = ICD_CODE_GROUPS[random.nextInt(ICD_CODE_GROUPS.length)];
                icdCodes.add(group[random.nextInt(group.length)]);
            }
            mapping.setIcdCodes(icdCodes);
            
            // Set random recommendation
            mapping.setDiagnosisRecommendation(RECOMMENDATIONS[random.nextInt(RECOMMENDATIONS.length)]);
            
            // Set random patient age (-120 to 120)
            mapping.setPatientAge(random.nextInt(241) - 120);
            
            // Set random NDC codes and class
            int ndcClassIndex = random.nextInt(NDC_CODE_CLASSES.length);
            mapping.setNdcCodeClass(NDC_CODE_CLASSES[ndcClassIndex]);
            
            // Generate random number of NDC codes (1-3)
            int numNdcCodes = random.nextInt(3) + 1;
            Set<String> ndcCodes = new HashSet<>();
            for (int j = 0; j < numNdcCodes; j++) {
                ndcCodes.add(NDC_CODES[ndcClassIndex][random.nextInt(NDC_CODES[ndcClassIndex].length)]);
            }
            mapping.setNdcCodes(ndcCodes);

            mongoRepository.save(mapping);
            elasticsearchRepository.save(mapping);

            if ((i + 1) % 50 == 0) {
                log.info("Generated {} mappings...", i + 1);
            }
        }

        log.info("Generated {} test documents", count);
    }

    public void cleanDatabases() {
        log.info("Cleaning databases");
        mongoRepository.deleteAll();
        elasticsearchRepository.deleteAll();
    }

    private String getRandomIcdCode() {
        String[] group = ICD_CODE_GROUPS[random.nextInt(ICD_CODE_GROUPS.length)];
        return group[random.nextInt(group.length)];
    }

    public String generateTestReport(List<Long> mongoTimes, List<Long> elasticsearchTimes) {
        double mongoAvg = mongoTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        double esAvg = elasticsearchTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        long mongoMin = mongoTimes.stream().mapToLong(Long::longValue).min().orElse(0);
        long esMin = elasticsearchTimes.stream().mapToLong(Long::longValue).min().orElse(0);
        long mongoMax = mongoTimes.stream().mapToLong(Long::longValue).max().orElse(0);
        long esMax = elasticsearchTimes.stream().mapToLong(Long::longValue).max().orElse(0);

        StringBuilder report = new StringBuilder();
        report.append("# ID-based Performance Test Results (3 minutes duration)\n\n");
        report.append("## MongoDB Performance\n");
        report.append(String.format("- Average Response Time: %.2fms\n", mongoAvg));
        report.append(String.format("- Minimum Response Time: %dms\n", mongoMin));
        report.append(String.format("- Maximum Response Time: %dms\n", mongoMax));
        report.append("\n## Elasticsearch Performance\n");
        report.append(String.format("- Average Response Time: %.2fms\n", esAvg));
        report.append(String.format("- Minimum Response Time: %dms\n", esMin));
        report.append(String.format("- Maximum Response Time: %dms\n", esMax));

        return report.toString();
    }

    public String runCombinedQueryTest() {
        log.info("Starting combined query performance test...");
        List<Long> mongoTimes = new ArrayList<>();
        List<Long> elasticsearchTimes = new ArrayList<>();

        // Test duration: 3 minutes
        long endTime = System.currentTimeMillis() + (3 * 60 * 1000);
        int queryCount = 0;

        while (System.currentTimeMillis() < endTime) {
            // Generate random query parameters
            int patientAge = random.nextInt(241) - 120;
            String ndcClass = NDC_CODE_CLASSES[random.nextInt(NDC_CODE_CLASSES.length)];
            String[] group = ICD_CODE_GROUPS[random.nextInt(ICD_CODE_GROUPS.length)];
            String icdCode = group[random.nextInt(group.length)];

            // MongoDB query
            long mongoStart = System.currentTimeMillis();
            mongoRepository.findByIcdCodesContainingAndPatientAgeBetweenAndNdcCodeClassAndNdcCodesContaining(
                icdCode, patientAge - 5, patientAge + 5, ndcClass, NDC_CODES[Arrays.asList(NDC_CODE_CLASSES).indexOf(ndcClass)][0]);
            long mongoTime = System.currentTimeMillis() - mongoStart;
            mongoTimes.add(mongoTime);

            // Elasticsearch query
            long elasticStart = System.currentTimeMillis();
            elasticsearchRepository.findByIcdCodesContainingAndPatientAgeBetweenAndNdcCodeClassAndNdcCodesContaining(
                icdCode, patientAge - 5, patientAge + 5, ndcClass, NDC_CODES[Arrays.asList(NDC_CODE_CLASSES).indexOf(ndcClass)][0]);
            long elasticTime = System.currentTimeMillis() - elasticStart;
            elasticsearchTimes.add(elasticTime);

            queryCount++;
            if (queryCount % 100 == 0) {
                log.info("Processed {} queries...", queryCount);
            }
        }

        // Calculate statistics
        double mongoAvg = mongoTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        double elasticAvg = elasticsearchTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        long mongoMin = mongoTimes.stream().mapToLong(Long::longValue).min().orElse(0);
        long elasticMin = elasticsearchTimes.stream().mapToLong(Long::longValue).min().orElse(0);
        long mongoMax = mongoTimes.stream().mapToLong(Long::longValue).max().orElse(0);
        long elasticMax = elasticsearchTimes.stream().mapToLong(Long::longValue).max().orElse(0);

        return String.format("""
            # Combined Query Performance Test Results (3 minutes duration)
            
            ## MongoDB Performance
            - Average Response Time: %.2fms
            - Minimum Response Time: %dms
            - Maximum Response Time: %dms
            
            ## Elasticsearch Performance
            - Average Response Time: %.2fms
            - Minimum Response Time: %dms
            - Maximum Response Time: %dms
            
            Total Queries Executed: %d
            """, mongoAvg, mongoMin, mongoMax, elasticAvg, elasticMin, elasticMax, queryCount);
    }
} 