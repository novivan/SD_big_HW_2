package com.example;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class HomeController {

    @GetMapping("/")
    public Mono<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "API Gateway is running");
        response.put("services", new String[] {
            "File Storing Service: /files/**",
            "File Analysis Service: /analysis/**"
        });
        response.put("documentation", "/swagger-ui.html");
        return Mono.just(response);
    }
}
