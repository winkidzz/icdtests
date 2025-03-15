package com.example.demo.util;

import com.example.demo.model.MyDocument;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@UtilityClass
public class TestDataGenerator {
    private static final Random random = new Random();
    
    // Sample ICD-10 codes and their descriptions
    private static final String[][] ICD_10_SAMPLES = {
        {"A15.0", "Tuberculosis of lung, confirmed by sputum microscopy"},
        {"E11.9", "Type 2 diabetes mellitus without complications"},
        {"I10", "Essential (primary) hypertension"},
        {"J45.901", "Unspecified asthma with (acute) exacerbation"},
        {"K29.70", "Gastritis, unspecified, without bleeding"},
        {"M54.5", "Low back pain"},
        {"R51", "Headache"},
        {"Z23", "Encounter for immunization"},
        {"F41.1", "Generalized anxiety disorder"},
        {"H60.339", "Swimmer's ear, unspecified ear"}
    };

    private static final String[] RECOMMENDATION_TEMPLATES = {
        "Recommend immediate treatment with %s. Monitor patient closely for %s.",
        "Consider %s as first-line treatment. Follow up in %d weeks.",
        "Start conservative treatment with %s. If no improvement in %d days, consider %s.",
        "Initiate %s therapy. Schedule follow-up appointment in %d days.",
        "Begin treatment protocol with %s. Monitor %s levels regularly."
    };

    private static final String[] MEDICATIONS = {
        "antibiotics", "NSAIDs", "corticosteroids", "antihistamines", 
        "bronchodilators", "antihypertensives", "oral hypoglycemics"
    };

    private static final String[] MONITORING_PARAMS = {
        "blood pressure", "blood sugar", "respiratory function", 
        "inflammatory markers", "pain levels", "symptoms"
    };

    public static MyDocument generateDocument() {
        MyDocument document = new MyDocument();
        int index = random.nextInt(ICD_10_SAMPLES.length);
        LocalDateTime now = LocalDateTime.now();
        
        document.setIcdCode(ICD_10_SAMPLES[index][0]);
        document.setDiagnosisRecommendation(generateRecommendation());
        document.setCreatedAt(now);
        document.setLastUpdated(now);
        
        return document;
    }

    public static List<MyDocument> generateDocuments(int count) {
        List<MyDocument> documents = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            documents.add(generateDocument());
        }
        return documents;
    }

    private static String generateRecommendation() {
        String template = RECOMMENDATION_TEMPLATES[random.nextInt(RECOMMENDATION_TEMPLATES.length)];
        String medication = MEDICATIONS[random.nextInt(MEDICATIONS.length)];
        String monitoring = MONITORING_PARAMS[random.nextInt(MONITORING_PARAMS.length)];
        int duration = random.nextInt(8) + 1; // 1-8 weeks/days
        
        return String.format(template, medication, duration, monitoring)
            .replace("%s %s", "%s")
            .substring(0, Math.min(200, template.length())); // Ensure it's not too long
    }
} 