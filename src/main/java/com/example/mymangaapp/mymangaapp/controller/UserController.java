package com.example.mymangaapp.mymangaapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    
    @GetMapping("/users")
    public String hello() {
        return "Hello, my manga app!";
    }

}
