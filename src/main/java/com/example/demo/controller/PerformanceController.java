package com.example.demo.controller;

import com.example.demo.model.CdsHooksRequest;
import com.example.demo.model.CdsHooksResponse;
import com.example.demo.model.IcdCodeMapping;
import com.example.demo.service.PerformanceComparisonService;
import com.example.demo.service.PerformanceTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceComparisonService performanceService;
    private final PerformanceTestService testService;

    @PostMapping("/test/id-based")
    public ResponseEntity<String> runIdBasedTest() {
        String report = testService.runIdBasedTest();
        return ResponseEntity.ok(report);
    }

    @PostMapping("/test/clean")
    public ResponseEntity<String> cleanDatabases() {
        testService.cleanDatabases();
        return ResponseEntity.ok("Databases cleaned successfully");
    }

    @PostMapping("/test/generate")
    public ResponseEntity<String> generateTestData(@RequestParam(defaultValue = "100") int count) {
        testService.generateTestData(count);
        return ResponseEntity.ok("Generated " + count + " test documents");
    }

    @PostMapping("/test/combined")
    public ResponseEntity<String> runCombinedQueryTest() {
        return ResponseEntity.ok(testService.runCombinedQueryTest());
    }

    @PostMapping("/mapping")
    public ResponseEntity<IcdCodeMapping> saveMapping(@RequestBody IcdCodeMapping mapping) {
        IcdCodeMapping savedMapping = performanceService.saveDocument(mapping);
        return ResponseEntity.ok(savedMapping);
    }

    @GetMapping("/mapping/{diagnosisId}")
    public ResponseEntity<IcdCodeMapping> findById(@PathVariable String diagnosisId) {
        Optional<IcdCodeMapping> mapping = performanceService.findById(diagnosisId);
        return mapping.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<IcdCodeMapping>> searchText(@RequestParam String term) {
        List<IcdCodeMapping> results = performanceService.searchText(term);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/search/advanced")
    public ResponseEntity<List<IcdCodeMapping>> advancedSearch(
            @RequestParam(required = false) String diagnosis,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String ndcCodeClass,
            @RequestParam(required = false) String ndcCode) {
        List<IcdCodeMapping> results = performanceService.advancedSearch(
            diagnosis, minAge, maxAge, ndcCodeClass, ndcCode);
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/mapping/{diagnosisId}")
    public ResponseEntity<Void> deleteById(@PathVariable String diagnosisId) {
        performanceService.deleteById(diagnosisId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cds-hooks")
    public ResponseEntity<CdsHooksResponse> getRecommendations(@RequestBody CdsHooksRequest request) {
        // Extract patient age from birthDate
        int patientAge = calculateAge(request.getPrefetch().getPatient().getBirthDate());
        
        // Get recommendations based on the criteria
        List<IcdCodeMapping> recommendations = performanceService.findByIcdCodesContainingAndPatientAgeBetweenAndNdcCodeClassAndNdcCodesContaining(
            request.getContext().getDiagnoses().get(0), // Using first diagnosis code
            patientAge,
            patientAge,
            request.getPrefetch().getMedication().getNdcCodeClass(),
            request.getPrefetch().getMedication().getNdcCode()
        );

        // Convert recommendations to CDS Hooks response
        CdsHooksResponse response = new CdsHooksResponse();
        List<CdsHooksResponse.Card> cards = recommendations.stream()
            .map(mapping -> {
                CdsHooksResponse.Card card = new CdsHooksResponse.Card();
                card.setSummary("Recommendation for " + mapping.getDiagnosisRecommendation());
                card.setDetail(mapping.getDiagnosisRecommendation()); // Using diagnosis as detail
                card.setIndicator("info");
                
                CdsHooksResponse.Source source = new CdsHooksResponse.Source();
                source.setLabel("ICD Mapping Service");
                source.setUrl("https://example.com/icd-mapping");
                card.setSource(source);
                
                return card;
            })
            .collect(Collectors.toList());
        
        response.setCards(cards);
        return ResponseEntity.ok(response);
    }

    private int calculateAge(String birthDate) {
        // Simple age calculation - in production, use a proper date library
        LocalDate birth = LocalDate.parse(birthDate);
        return Period.between(birth, LocalDate.now()).getYears();
    }
} 