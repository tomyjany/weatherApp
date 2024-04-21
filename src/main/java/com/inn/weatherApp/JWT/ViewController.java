package com.inn.weatherApp.JWT;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {
    // Match all requests that should be handled by Angular
    @GetMapping(value = "/{path:[^\\.]*}")
    public String forwardToIndex() {
        return "forward:/index.html";
    }
    @GetMapping("/")
    public String redirectRoot() {
        return "redirect:/index.html";
    }
}
