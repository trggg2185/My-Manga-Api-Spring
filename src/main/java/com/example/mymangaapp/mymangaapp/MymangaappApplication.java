package com.example.mymangaapp.mymangaapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // Kích hoạt Auditing
public class MymangaappApplication {
	public static void main(String[] args) {
		SpringApplication.run(MymangaappApplication.class, args);
	}

}
