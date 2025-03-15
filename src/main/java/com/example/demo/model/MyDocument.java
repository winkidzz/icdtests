package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Document(collection = "diagnoses")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "diagnoses")
public class MyDocument implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Indexed
    @Field(type = FieldType.Keyword)
    private String icdCode;

    @Field(type = FieldType.Text)
    private String diagnosisRecommendation;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime lastUpdated;
} 