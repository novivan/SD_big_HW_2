package com.example;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.result.view.RedirectView;

@Controller
public class SwaggerController {

    @GetMapping("/swagger")
    public RedirectView redirectToSwaggerUI() {
        return new RedirectView("/swagger-ui.html");
    }
}
