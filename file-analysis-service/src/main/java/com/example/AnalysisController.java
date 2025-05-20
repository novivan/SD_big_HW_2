package com.example;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private AnalysisResultRepository analysisResultRepository;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/{fileId}")
    public ResponseEntity<AnalysisResult> analyzeFile(@PathVariable String fileId) {
        try {
            //getting file from File Storing Service
            String fileServiceUrl = "http://localhost:5001/files/" + fileId;
            ResponseEntity<FileEntity> response = restTemplate.getForEntity(fileServiceUrl, FileEntity.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            FileEntity file = response.getBody();
            String content = file.getContent();

            //analyzing file
            AnalysisResult result = new AnalysisResult();
            result.setFileId(fileId);
            result.setParagraphs(content.split("\n").length);
            result.setWords(content.split("\\s+").length);
            result.setCharacters(content.length());

            // Simulating plagiarism check
            List<FileEntity> allFiles = restTemplate.getForObject("http://localhost:5001/files", List.class);
            result.setPlagiarized(allFiles.stream().anyMatch(f -> f.getContent().equals(content) && !f.getFileId().equals(fileId)));
            analysisResultRepository.save(result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/{fileId}")
    public ResponseEntity<AnalysisResult> getAnalysis(@PathVariable String fileId) {
        return analysisResultRepository.findById(fileId)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{fileId}/wordcloud")
    public ResponseEntity<String> getWordCloud(@PathVariable String fileId) {
        try {
            // Получаем результаты анализа
            ResponseEntity<AnalysisResult> analysisResponse = getAnalysis(fileId);
            if (!analysisResponse.getStatusCode().is2xxSuccessful() || analysisResponse.getBody() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Analysis not found");
            }
            
            // Получаем содержимое файла
            String fileServiceUrl = "http://localhost:5001/files/" + fileId;
            ResponseEntity<FileEntity> fileResponse = restTemplate.getForEntity(fileServiceUrl, FileEntity.class);
            if (!fileResponse.getStatusCode().is2xxSuccessful() || fileResponse.getBody() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
            }
            
            FileEntity file = fileResponse.getBody();
            
            // Здесь должна быть реализация создания облака слов с использованием QuickChart API
            // Для примера просто возвращаем ссылку на API
            String wordCloudUrl = "https://quickchart.io/wordcloud?text=" + 
                                 java.net.URLEncoder.encode(file.getContent(), "UTF-8");
            
            return ResponseEntity.ok("Word cloud URL: " + wordCloudUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating word cloud: " + e.getMessage());
        }
    }
}