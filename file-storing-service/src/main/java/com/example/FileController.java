package com.example;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/files")
@Tag(name = "File Storage API", description = "API для загрузки и получения файлов")
public class FileController {
    @Autowired
    private FileRepository fileRepository;

    @PostMapping
    @Operation(
        summary = "Загрузка файла",
        description = "Загружает текстовый файл в систему. Допускаются только файлы .txt",
        responses = {
            @ApiResponse(responseCode = "200", description = "Успешно загружен, возвращает ID файла"),
            @ApiResponse(responseCode = "400", description = "Неверный формат файла"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
        }
    )
    public ResponseEntity<String> uploadFile(
            @Parameter(description = "Текстовый файл .txt", required = true)
            @RequestParam("file") MultipartFile file) {
        try {
            if (!file.getOriginalFilename().endsWith(".txt")) {
                return ResponseEntity.badRequest().body("Only .txt files allowed.");
            }
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFileId(UUID.randomUUID().toString());
            fileEntity.setFileName(file.getOriginalFilename());
            fileEntity.setContent(new String(file.getBytes()));
            fileRepository.save(fileEntity);
            return ResponseEntity.ok(fileEntity.getFileId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading File: " + e.getMessage());
        }
    }

    @GetMapping("/{fileId}")
    @Operation(
        summary = "Получение файла по ID",
        description = "Возвращает файл по указанному ID",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Файл найден",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = FileEntity.class))
            ),
            @ApiResponse(responseCode = "404", description = "Файл не найден")
        }
    )
    public ResponseEntity<FileEntity> getFile(
            @Parameter(description = "ID файла", required = true)
            @PathVariable String fileId) {
        return fileRepository.findById(fileId)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Operation(
        summary = "Получение всех файлов",
        description = "Возвращает список всех загруженных файлов",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Список файлов",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = FileEntity.class))
            )
        }
    )
    public ResponseEntity<List<FileEntity>> getAllFiles() {
        return ResponseEntity.ok(fileRepository.findAll());
    }
}
