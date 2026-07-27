package com.sentinelai.platform.testsupport;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class BasePostgresTest {

    static PostgreSQLContainer<?> postgresContainer = PostgreSQLTestContainer.postgreSQLContainer;

    static {
        if(!postgresContainer.isRunning()) {
            postgresContainer.start();
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {

        registry.add(
                "spring.datasource.url",
                postgresContainer::getJdbcUrl
        );

        registry.add(
                "spring.datasource.username",
                postgresContainer::getUsername
        );

        registry.add(
                "spring.datasource.password",
                postgresContainer::getPassword
        );
    }
}