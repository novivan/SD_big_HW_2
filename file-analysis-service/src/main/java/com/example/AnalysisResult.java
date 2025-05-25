package com.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "analysis_results")
public class AnalysisResult {
    @Id
    private String fileId;
    private int words;
    private int characters;
    private int paragraphs;
    private boolean plagiarized;

    // Getters и Setters
    public String getFileId() {
        return fileId;
    }
    public void setFileId(String fileId) {
        this.fileId = fileId;
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
    public int getParagraphs() {
        return paragraphs;
    }
    public void setParagraphs(int paragraphs) {
        this.paragraphs = paragraphs;
    }
    public boolean isPlagiarized() {
        return plagiarized;
    }
    public void setPlagiarized(boolean plagiarized) {
        this.plagiarized = plagiarized;
    }
}