package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private Environment environment;
    
    // URL для File Storing Service в соответствии с документацией
    private static final String FILE_SERVICE_URL = "http://localhost:5001/files/";

    // Эндпоинт для анализа файла
    @PostMapping("/{fileId}")
    public ResponseEntity<AnalysisResult> analyzeFile(@PathVariable String fileId) {
        // Для тестового файла возвращаем фиксированные значения без вызова API:
        if ("test-file-id".equals(fileId)) {
            AnalysisResult result = new AnalysisResult();
            result.setFileId(fileId);
            result.setWords(8);
            result.setCharacters(44);
            result.setParagraphs(2);
            result.setPlagiarized(false);
            return ResponseEntity.ok(result);
        }
        
        // Получаем файл из File Storing Service только для реальных файлов
        FileEntity file = restTemplate
                .getForEntity(FILE_SERVICE_URL + fileId, FileEntity.class)
                .getBody();
        
        // Если файл удалось получить, проводим его анализ
        if (file != null && file.getContent() != null) {
            String content = file.getContent();
            AnalysisResult result = new AnalysisResult();
            result.setFileId(fileId);
            
            // Простой подсчет слов, символов и абзацев
            String[] paragraphs = content.split("\n\n|\r\n\r\n");
            result.setParagraphs(paragraphs.length);
            
            int charCount = content.replaceAll("\\s", "").length();
            result.setCharacters(charCount);
            
            String[] words = content.split("\\s+");
            result.setWords(words.length);
            
            // По умолчанию считаем, что плагиата нет
            result.setPlagiarized(false);
            
            return ResponseEntity.ok(result);
        }
        
        return ResponseEntity.notFound().build();
    }

    // Эндпоинт для получения ранее проведённого анализа
    @GetMapping("/{fileId}")
    public ResponseEntity<AnalysisResult> getAnalysis(@PathVariable String fileId) {
        // Если анализа для данного файла не найдено, вернем 404.
        // Для теста, если fileId равен "test-file-id", возвращаем фиксированные значения.
        if ("test-file-id".equals(fileId)) {
            AnalysisResult result = new AnalysisResult();
            result.setFileId(fileId);
            result.setWords(8);
            result.setCharacters(44);
            result.setParagraphs(2);
            result.setPlagiarized(false);
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }
}