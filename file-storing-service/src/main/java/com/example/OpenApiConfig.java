package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fileStorageOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("File Storage Service API")
                        .version("1.0")
                        .description("API для загрузки и получения файлов")
                        .contact(new Contact()
                                .name("File Storage Team")
                                .email("example@example.com")));
    }
}
