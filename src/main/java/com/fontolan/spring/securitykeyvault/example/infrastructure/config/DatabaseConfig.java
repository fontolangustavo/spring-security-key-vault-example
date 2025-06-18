package com.fontolan.spring.securitykeyvault.example.infrastructure.config;

import com.fontolan.spring.securitykeyvault.example.application.service.SecretsManagerService;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {
    private final SecretsManagerService secretsManagerService;

    public DatabaseConfig(SecretsManagerService secretsManagerService) {
        this.secretsManagerService = secretsManagerService;
    }

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url("jdbc:postgresql://localhost:5432/products_db")
                .username("postgres")
                .password("postgres")
                .build();
    }
}
