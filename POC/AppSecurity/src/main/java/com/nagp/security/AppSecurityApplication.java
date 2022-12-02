package com.nagp.security;

import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nagp.security.models.Role;
import com.nagp.security.models.User;
import com.nagp.security.repo.RoleRepo;
import com.nagp.security.repo.UserRepo;
import com.nagp.security.service.UserService;

@SpringBootApplication

public class AppSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppSecurityApplication.class, args);
	}
	
	@Bean
	CommandLineRunner run(UserService service) {
		return args -> {
			service.saveRole(new Role(null, "ROLE_USER"));
			service.saveRole(new Role(null, "ROLE_ADMIN"));
			service.saveRole(new Role(null, "ROLE_SELLER"));
			
			service.saveUser(new User(null, "Abhishek Kashyap", "abhishek", "1234", new ArrayList<>()));
			service.addRoleToUser("abhishek", "ROLE_USER");
			service.addRoleToUser("abhishek", "ROLE_ADMIN");
		};
	}
	
	@Bean
	public PasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
