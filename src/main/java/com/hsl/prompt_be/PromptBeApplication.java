package com.hsl.prompt_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class PromptBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(PromptBeApplication.class, args);
	}

}
