package com.energia.resourcemanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.energia.resourcemanagement.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfig {
}