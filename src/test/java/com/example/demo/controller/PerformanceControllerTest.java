package com.example.demo.controller;

import com.example.demo.config.TestConfig;
import com.example.demo.service.PerformanceTestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
@ActiveProfiles("test")
public class PerformanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PerformanceTestService testService;

    @Test
    void runIdBasedTest_ShouldReturnReport() throws Exception {
        // Given
        String expectedReport = "Test Report";
        when(testService.runIdBasedTest()).thenReturn(expectedReport);

        // When & Then
        mockMvc.perform(post("/api/performance/test/id-based"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedReport));
    }

    @Test
    void cleanDatabases_ShouldReturnSuccess() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/performance/test/clean"))
                .andExpect(status().isOk())
                .andExpect(content().string("Databases cleaned successfully"));
    }
} 