# ICD Code Mapping Service

A Spring Boot application that provides ICD code mapping functionality with performance comparison between MongoDB and Elasticsearch implementations.

## Features

- ICD code mapping management
- Performance comparison between MongoDB and Elasticsearch
- RESTful API for mapping operations
- Performance testing capabilities
- CDS Hooks integration

## Prerequisites

- Java 21 or higher
- Maven 3.8 or higher
- MongoDB 4.4 or higher
- Elasticsearch 8.x
- Docker (for test containers)

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           └── demo/
│   │               ├── config/
│   │               ├── controller/
│   │               ├── model/
│   │               ├── repository/
│   │               └── service/
│   └── resources/
│       └── application.properties
└── test/
    ├── java/
    │   └── com/
    │       └── example/
    │           └── demo/
    │               ├── config/
    │               ├── controller/
    │               ├── service/
    │               └── PerformanceComparisonServiceTest.java
    └── resources/
        └── application.properties
```

## API Documentation

### ICD Mapping Operations

#### Create Mapping
```bash
POST /api/performance/mapping
Content-Type: application/json

{
  "diagnosisRecommendation": "Type 2 Diabetes with Hyperglycemia",
  "patientAge": 45,
  "ndcCodeClass": "Antidiabetics",
  "ndcCodes": ["0002-3228-01"],
  "icdCodes": ["E11.65"]
}
```

#### Get Mapping by ID
```bash
GET /api/performance/mapping/{diagnosisId}
```

#### Search Mappings
```bash
GET /api/performance/search?term=diabetes
```

#### Advanced Search
```bash
GET /api/performance/search/advanced?diagnosis=diabetes&minAge=40&maxAge=50&ndcCodeClass=Antidiabetics&ndcCode=0002-3228
```

#### Delete Mapping
```bash
DELETE /api/performance/mapping/{diagnosisId}
```

### Performance Testing Endpoints

#### Run ID-based Test
```bash
POST /api/performance/test/id-based
```

#### Clean Databases
```bash
POST /api/performance/test/clean
```

#### Generate Test Data
```bash
POST /api/performance/test/generate?count=100
```

#### Run Combined Query Test
```bash
POST /api/performance/test/combined
```

### CDS Hooks Integration

#### Get Recommendations
```bash
POST /api/performance/cds-hooks
Content-Type: application/json

{
  "context": {
    "diagnoses": ["E11.65"]
  },
  "prefetch": {
    "patient": {
      "birthDate": "1978-04-19"
    },
    "medication": {
      "ndcCodeClass": "Antidiabetics",
      "ndcCode": "0002-3228-01"
    }
  }
}
```

## Setup and Running

1. Clone the repository
2. Configure MongoDB and Elasticsearch in `src/main/resources/application.properties`
3. Build the project:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Testing

### Unit Tests
```bash
mvn test
```

### Performance Tests
```bash
mvn test -Dtest=PerformanceComparisonServiceTest
```

## Configuration

### Main Application Properties
```properties
# Server Configuration
server.port=8080
spring.application.name=document-service

# MongoDB Configuration
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=documentdb

# Elasticsearch Configuration
spring.elasticsearch.uris=http://localhost:9200

# Cache Configuration
spring.cache.type=redis
spring.cache.redis.time-to-live=3600000
spring.cache.redis.cache-null-values=false

# Performance Tuning
spring.data.mongodb.auto-index-creation=true
server.tomcat.max-threads=200
server.tomcat.accept-count=100
```

### Test Properties
```properties
# Test Configuration
spring.profiles.active=test
server.port=0
spring.cache.type=none

# TestContainers Configuration
testcontainers.reuse.enable=true
```

## Performance Monitoring

The service automatically logs performance metrics for:
- Document save operations
- ID-based queries
- Text searches
- Advanced searches
- Delete operations

Example log output:
```
Save times - MongoDB: 15ms, Elasticsearch: 31ms
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 