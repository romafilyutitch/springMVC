package com.epam.esm.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@EntityScan(basePackages = {"com.epam.esm.model"})
@EnableJpaRepositories(basePackages = {"com.epam.esm.repository"})
public class PersistanceConfiguration {
}
