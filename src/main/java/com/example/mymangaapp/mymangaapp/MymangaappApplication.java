package com.example.mymangaapp.mymangaapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing // Kích hoạt Auditing
@EnableScheduling
public class MymangaappApplication {
	public static void main(String[] args) {
		SpringApplication.run(MymangaappApplication.class, args);
	}

}
