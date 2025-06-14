package com.fontolan.spring.securitykeyvault.example;

import com.fontolan.spring.securitykeyvault.example.domain.model.User;
import com.fontolan.spring.securitykeyvault.example.application.service.UserService;
import com.fontolan.spring.securitykeyvault.example.domain.model.Role;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class SpringSecurityKeyVaultExampleApplication {

        public static void main(String[] args) {
                SpringApplication.run(SpringSecurityKeyVaultExampleApplication.class, args);
        }

        @Bean
        CommandLineRunner init(UserService userService) {
                return args -> {
                        if (userService.findAll().isEmpty()) {
                                User user = new User();
                                user.setUsername("admin");
                                user.setPassword("admin");
                                user.setRoles(java.util.Set.of(Role.ROLE_USER, Role.ROLE_ADMIN));
                                userService.save(user);
                        }
                };
        }

}
