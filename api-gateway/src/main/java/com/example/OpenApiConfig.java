package com.example;

import java.util.ArrayList;
import java.util.List;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

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
                                .email("example@example.com")))
                .addServersItem(new Server().url("http://localhost:8080"))
                .addServersItem(new Server().url("http://localhost:5001").description("File Storing Service"))
                .addServersItem(new Server().url("http://localhost:5002").description("File Analysis Service"));
    }
    
    @Bean
    @Lazy(false)
    public List<GroupedOpenApi> apis(RouteDefinitionLocator locator) {
        List<GroupedOpenApi> groups = new ArrayList<>();
        
        // Создаем группу для API Gateway
        groups.add(GroupedOpenApi.builder()
                .group("gateway")
                .pathsToMatch("/**")
                .build());
        
        // Создаем группы для каждого сервиса
        groups.add(GroupedOpenApi.builder()
                .group("file-storing")
                .pathsToMatch("/files/**")
                .build());
                
        groups.add(GroupedOpenApi.builder()
                .group("file-analysis")
                .pathsToMatch("/analysis/**")
                .build());
        
        return groups;
    }
}
