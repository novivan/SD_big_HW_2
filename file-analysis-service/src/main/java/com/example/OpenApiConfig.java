package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fileAnalysisOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("File Analysis Service API")
                        .version("1.0")
                        .description("API для анализа файлов, проверки на плагиат и создания облака слов")
                        .contact(new Contact()
                                .name("File Analysis Team")
                                .email("example@example.com")));
    }
}
