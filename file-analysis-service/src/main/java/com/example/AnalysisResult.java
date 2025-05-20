package com.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class AnalysisResult
{
    @Id 
    private String fileId;
    private int paragraphs;
    private int words;
    private int characters;
    private boolean isPlagiarized;

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
        return isPlagiarized;
    }

    public void setPlagiarized(boolean isPlagiarized) {
        this.isPlagiarized = isPlagiarized;
    }
}