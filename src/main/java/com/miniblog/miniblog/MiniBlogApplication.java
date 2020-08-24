package com.miniblog.miniblog;

import com.miniblog.miniblog.models.data.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
public class MiniBlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiniBlogApplication.class, args);
	}

}
