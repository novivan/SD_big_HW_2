package com.example;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        RestTemplate mockRestTemplate = Mockito.mock(RestTemplate.class);

        // Создаем тестовый файл
        FileEntity testFile = new FileEntity();
        testFile.setFileId("test-file-id");
        testFile.setFileName("test.txt");
        testFile.setContent("Hello World\nThis is a test file");

        // Настраиваем мок для запроса конкретного файла
        Mockito.when(mockRestTemplate.getForEntity("http://localhost:5001/files/test-file-id", FileEntity.class))
               .thenReturn(new ResponseEntity<>(testFile, HttpStatus.OK));

        // Настраиваем мок для запроса всех файлов
        FileEntity[] allFiles = new FileEntity[] { testFile };
        Mockito.when(mockRestTemplate.getForEntity("http://localhost:5001/files", FileEntity[].class))
               .thenReturn(new ResponseEntity<>(allFiles, HttpStatus.OK));

        return mockRestTemplate;
    }
}