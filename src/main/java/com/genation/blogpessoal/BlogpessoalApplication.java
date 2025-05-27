package com.genation.blogpessoal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.genation.blogpessoal")
@EnableJpaRepositories(basePackages = "com.genation.blogpessoal.repository")
@EntityScan(basePackages = "com.genation.blogpessoal.model")
public class BlogpessoalApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogpessoalApplication.class, args);
	}

}
