package com.example.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Set;

@Data
@org.springframework.data.mongodb.core.mapping.Document(collection = "icd_mappings")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "icd_mappings")
public class IcdCodeMapping {
    @Id
    private String id;
    
    @Field(type = FieldType.Keyword)
    private String diagnosisId;
    
    @Field(type = FieldType.Keyword)
    private Set<String> icdCodes;
    
    @Field(type = FieldType.Text)
    private String diagnosisRecommendation;
    
    @Field(type = FieldType.Integer)
    private Integer patientAge;
    
    @Field(type = FieldType.Keyword)
    private Set<String> ndcCodes;
    
    @Field(type = FieldType.Keyword)
    private String ndcCodeClass;
} 