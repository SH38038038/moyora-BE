package com.project.moyora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MoyoraApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoyoraApplication.class, args);
	}

}
