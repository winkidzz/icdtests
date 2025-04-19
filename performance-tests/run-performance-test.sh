#!/bin/bash

# Check if JMeter is installed
if ! command -v jmeter &> /dev/null; then
    echo "JMeter is not installed. Please install JMeter first."
    echo "You can install it using: brew install jmeter"
    exit 1
fi

# Start the Spring Boot application in the background
echo "Starting Spring Boot application..."
mvn spring-boot:run &
SPRING_BOOT_PID=$!

# Wait for the application to start
echo "Waiting for application to start..."
sleep 30

# Run JMeter test
echo "Running JMeter performance test..."
jmeter -n -t ICDPerformanceTest.jmx -l performance_results.jtl -e -o performance_report

# Stop the Spring Boot application
echo "Stopping Spring Boot application..."
kill $SPRING_BOOT_PID

echo "Performance test completed. Results are in performance_report directory." 