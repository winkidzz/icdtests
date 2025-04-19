package com.example.demo.model;

import lombok.Data;
import java.util.List;

@Data
public class CdsHooksRequest {
    private String hook;
    private String hookInstance;
    private Prefetch prefetch;
    private Context context;

    @Data
    public static class Prefetch {
        private Patient patient;
        private Medication medication;
    }

    @Data
    public static class Patient {
        private String id;
        private String gender;
        private String birthDate;
    }

    @Data
    public static class Medication {
        private String rxnormCode;
        private String ndcCode;
        private String ndcCodeClass;
    }

    @Data
    public static class Context {
        private List<String> diagnoses;
        private String userId;
        private String patientId;
    }
} 