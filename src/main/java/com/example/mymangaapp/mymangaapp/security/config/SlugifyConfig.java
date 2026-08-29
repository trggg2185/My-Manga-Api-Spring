package com.example.mymangaapp.mymangaapp.security.config;

import com.github.slugify.Slugify;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlugifyConfig {

    @Bean
    Slugify slugify() {
        return new Slugify();
    }
}
