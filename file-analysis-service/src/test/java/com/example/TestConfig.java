package com.example;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Test configuration that provides test implementations for external dependencies
 */
@TestConfiguration
public class TestConfig {

    /**
     * Creates a test RestTemplate that returns predefined responses
     * instead of making real HTTP calls
     */
    @Bean
    @Primary
    public RestTemplate testRestTemplate() {
        return new TestRestTemplate();
    }
    
    /**
     * Custom RestTemplate implementation for testing
     */
    static class TestRestTemplate extends RestTemplate {
        @Override
        public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Object... uriVariables) {
            if (url.contains("/files/") && responseType.equals(FileEntity.class)) {
                FileEntity mockFile = new FileEntity();
                mockFile.setFileId("test-file-id");
                mockFile.setFileName("test-file.txt");
                mockFile.setContent("This is a test content.\nWith multiple lines.");
                return (ResponseEntity<T>) new ResponseEntity<>(mockFile, HttpStatus.OK);
            }
            return (ResponseEntity<T>) ResponseEntity.notFound().build();
        }
        
        @Override
        public <T> T getForObject(String url, Class<T> responseType, Object... uriVariables) {
            if (url.contains("/files") && responseType.equals(List.class)) {
                List<FileEntity> mockFiles = new ArrayList<>();
                FileEntity mockFile = new FileEntity();
                mockFile.setFileId("test-file-id");
                mockFile.setFileName("test-file.txt");
                mockFile.setContent("This is a test content.\nWith multiple lines.");
                mockFiles.add(mockFile);
                return (T) mockFiles;
            }
            return null;
        }
    }
}