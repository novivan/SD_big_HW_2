package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
public class AnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAnalyzeFile() throws Exception {
        mockMvc.perform(post("/analysis/test-file-id"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.fileId").value("test-file-id"))
               .andExpect(jsonPath("$.words").value(8))
               .andExpect(jsonPath("$.characters").value(44))
               .andExpect(jsonPath("$.paragraphs").value(2));
    }
    
    @Test
    public void testGetAnalysis() throws Exception {
        // First create the analysis
        mockMvc.perform(post("/analysis/test-file-id")).andExpect(status().isOk());
        
        // Then retrieve it
        mockMvc.perform(get("/analysis/test-file-id"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.fileId").value("test-file-id"))
               .andExpect(jsonPath("$.words").value(8))
               .andExpect(jsonPath("$.characters").value(44))
               .andExpect(jsonPath("$.paragraphs").value(2))
               .andExpect(jsonPath("$.plagiarized").value(false));
    }
}