package com.example;

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


@RestController
@RequestMapping("/files")
public class FileController {
    @Autowired
    private FileRepository fileRepository;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
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
    public ResponseEntity<FileEntity> getFile(@PathVariable String fileId) {
        return fileRepository.findById(fileId)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
