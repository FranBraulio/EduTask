package com.edutask.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class eduTaskController {
    @GetMapping("/")
    public String index() {
        return "index.html";
    }
}
