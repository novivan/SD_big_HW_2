package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Система анализа студенческих отчетов API")
                        .version("1.0")
                        .description("Документация API для системы анализа студенческих отчетов")
                        .contact(new Contact()
                                .name("Команда разработки")
                                .email("example@example.com")));
    }
}
