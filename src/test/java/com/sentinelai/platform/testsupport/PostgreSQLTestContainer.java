package com.sentinelai.platform.testsupport;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgreSQLTestContainer {

    public static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("sentinel_ai_test")
                    .withUsername("test")
                    .withPassword("test");
}