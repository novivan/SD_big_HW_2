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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/analysis")
@Tag(name = "File Analysis API", description = "API для анализа файлов и проверки на плагиат")
public class AnalysisController {
    @Autowired
    private AnalysisResultRepository analysisResultRepository;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/{fileId}")
    @Operation(
        summary = "Анализ файла",
        description = "Анализирует файл по указанному ID, подсчитывает статистику и проверяет на плагиат",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Анализ успешно выполнен",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnalysisResult.class))
            ),
            @ApiResponse(responseCode = "404", description = "Файл не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
        }
    )
    public ResponseEntity<AnalysisResult> analyzeFile(
            @Parameter(description = "ID файла", required = true)
            @PathVariable String fileId) {
        try {
            // Получаем файл из File Storing Service
            String fileServiceUrl = "http://localhost:5001/files/" + fileId;
            ResponseEntity<FileEntity> response = restTemplate.getForEntity(fileServiceUrl, FileEntity.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            FileEntity file = response.getBody();
            String content = file.getContent();

            // Анализ файла
            AnalysisResult result = new AnalysisResult();
            result.setFileId(fileId);
            result.setParagraphs(content.split("\n").length);
            result.setWords(content.split("\\s+").length);
            result.setCharacters(content.length());

            // Проверка на плагиат
            List<FileEntity> allFiles = restTemplate.getForObject("http://localhost:5001/files", List.class);
            result.setPlagiarized(allFiles.stream().anyMatch(f -> f.getContent().equals(content) && !f.getFileId().equals(fileId)));
            analysisResultRepository.save(result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/{fileId}")
    @Operation(
        summary = "Получение результатов анализа",
        description = "Возвращает результаты анализа файла по указанному ID",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Результаты анализа найдены",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnalysisResult.class))
            ),
            @ApiResponse(responseCode = "404", description = "Результаты анализа не найдены")
        }
    )
    public ResponseEntity<AnalysisResult> getAnalysis(
            @Parameter(description = "ID файла", required = true)
            @PathVariable String fileId) {
        return analysisResultRepository.findById(fileId)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{fileId}/wordcloud")
    @Operation(
        summary = "Генерация облака слов",
        description = "Генерирует облако слов для файла и возвращает URL",
        responses = {
            @ApiResponse(responseCode = "200", description = "URL облака слов"),
            @ApiResponse(responseCode = "404", description = "Файл или результаты анализа не найдены"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
        }
    )
    public ResponseEntity<String> getWordCloud(
            @Parameter(description = "ID файла", required = true)
            @PathVariable String fileId) {
        try {
            // Получаем результаты анализа
            if (!analysisResultRepository.existsById(fileId)) {
                // Если анализ еще не проводился, запускаем его
                ResponseEntity<AnalysisResult> analysisResponse = analyzeFile(fileId);
                if (!analysisResponse.getStatusCode().is2xxSuccessful() || analysisResponse.getBody() == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Analysis could not be performed");
                }
            }
            
            // Получаем содержимое файла
            String fileServiceUrl = "http://localhost:5001/files/" + fileId;
            ResponseEntity<FileEntity> fileResponse = restTemplate.getForEntity(fileServiceUrl, FileEntity.class);
            if (!fileResponse.getStatusCode().is2xxSuccessful() || fileResponse.getBody() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
            }
            
            FileEntity file = fileResponse.getBody();
            
            // Генерация облака слов с использованием QuickChart API
            String wordCloudUrl = "https://quickchart.io/wordcloud?text=" + 
                                 java.net.URLEncoder.encode(file.getContent(), "UTF-8");
            
            return ResponseEntity.ok("Word cloud URL: " + wordCloudUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating word cloud: " + e.getMessage());
        }
    }
    
    @GetMapping("/{fileId}/plagiarism")
    @Operation(
        summary = "Проверка на плагиат",
        description = "Возвращает результат проверки файла на плагиат",
        responses = {
            @ApiResponse(responseCode = "200", description = "Результат проверки на плагиат"),
            @ApiResponse(responseCode = "404", description = "Файл или результаты анализа не найдены"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
        }
    )
    public ResponseEntity<String> checkPlagiarism(
            @Parameter(description = "ID файла", required = true)
            @PathVariable String fileId) {
        try {
            // Получаем результаты анализа
            return analysisResultRepository.findById(fileId)
                .map(result -> {
                    boolean isPlagiarized = result.isPlagiarized();
                    String message = isPlagiarized 
                        ? "Внимание: Файл содержит плагиат!" 
                        : "Плагиат не обнаружен.";
                    return ResponseEntity.ok(message);
                })
                .orElseGet(() -> {
                    // Если анализ еще не проводился, запускаем его
                    ResponseEntity<AnalysisResult> analysisResponse = analyzeFile(fileId);
                    if (!analysisResponse.getStatusCode().is2xxSuccessful() || analysisResponse.getBody() == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Analysis could not be performed");
                    }
                    
                    AnalysisResult result = analysisResponse.getBody();
                    boolean isPlagiarized = result.isPlagiarized();
                    String message = isPlagiarized 
                        ? "Внимание: Файл содержит плагиат!" 
                        : "Плагиат не обнаружен.";
                    return ResponseEntity.ok(message);
                });
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error checking plagiarism: " + e.getMessage());
        }
    }
    
    @GetMapping("/{fileId}/statistics")
    @Operation(
        summary = "Получение статистики файла",
        description = "Возвращает статистику файла (абзацы, слова, символы)",
        responses = {
            @ApiResponse(responseCode = "200", description = "Статистика файла"),
            @ApiResponse(responseCode = "404", description = "Файл или результаты анализа не найдены"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
        }
    )
    public ResponseEntity<String> getStatistics(
            @Parameter(description = "ID файла", required = true)
            @PathVariable String fileId) {
        try {
            // Получаем результаты анализа
            return analysisResultRepository.findById(fileId)
                .map(result -> {
                    String stats = String.format(
                        "Статистика файла:\n- Абзацев: %d\n- Слов: %d\n- Символов: %d",
                        result.getParagraphs(), result.getWords(), result.getCharacters()
                    );
                    return ResponseEntity.ok(stats);
                })
                .orElseGet(() -> {
                    // Если анализ еще не проводился, запускаем его
                    ResponseEntity<AnalysisResult> analysisResponse = analyzeFile(fileId);
                    if (!analysisResponse.getStatusCode().is2xxSuccessful() || analysisResponse.getBody() == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Analysis could not be performed");
                    }
                    
                    AnalysisResult result = analysisResponse.getBody();
                    String stats = String.format(
                        "Статистика файла:\n- Абзацев: %d\n- Слов: %d\n- Символов: %d",
                        result.getParagraphs(), result.getWords(), result.getCharacters()
                    );
                    return ResponseEntity.ok(stats);
                });
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error getting statistics: " + e.getMessage());
        }
    }
}