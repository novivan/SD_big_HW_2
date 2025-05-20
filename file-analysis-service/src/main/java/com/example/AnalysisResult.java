package com.example;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@Schema(description = "Результаты анализа файла")
public class AnalysisResult
{
    @Id
    @Schema(description = "ID файла", example = "550e8400-e29b-41d4-a716-446655440000")
    private String fileId;
    
    @Schema(description = "Количество абзацев", example = "5")
    private int paragraphs;
    
    @Schema(description = "Количество слов", example = "250")
    private int words;
    
    @Schema(description = "Количество символов", example = "1500")
    private int characters;
    
    @Schema(description = "Признак плагиата", example = "false")
    private boolean plagiarized;
    
    @Schema(description = "URL облака слов", example = "https://quickchart.io/wordcloud?text=...")
    private String wordCloudUrl;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public int getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(int paragraphs) {
        this.paragraphs = paragraphs;
    }

    public int getWords() {
        return words;
    }

    public void setWords(int words) {
        this.words = words;
    }

    public int getCharacters() {
        return characters;
    }

    public void setCharacters(int characters) {
        this.characters = characters;
    }

    public boolean isPlagiarized() {
        return plagiarized;
    }

    public void setPlagiarized(boolean plagiarized) {
        this.plagiarized = plagiarized;
    }

    public String getWordCloudUrl() {
        return wordCloudUrl;
    }

    public void setWordCloudUrl(String wordCloudUrl) {
        this.wordCloudUrl = wordCloudUrl;
    }
}