package com.example.demo.controller;

import com.example.demo.model.IcdCodeMapping;
import com.example.demo.service.PerformanceComparisonService;
import com.example.demo.service.PerformanceTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @DeleteMapping("/mapping/{diagnosisId}")
    public ResponseEntity<Void> deleteById(@PathVariable String diagnosisId) {
        performanceService.deleteById(diagnosisId);
        return ResponseEntity.ok().build();
    }
} 