package com.sd4.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@ComponentScan({"com.sd4.service", "com.sd4.controller"})
@EntityScan("com.sd4.model")
@EnableJpaRepositories("com.sd4.repository")
public class AssignmentTwo2022Application {

	public static void main(String[] args) {
		SpringApplication.run(AssignmentTwo2022Application.class, args);
	}
        @Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/beer").allowedOrigins("http://localhost:8888");
                                registry.addMapping("/brewery").allowedOrigins("http://localhost:8888");

			}
		};
	}
        

}
