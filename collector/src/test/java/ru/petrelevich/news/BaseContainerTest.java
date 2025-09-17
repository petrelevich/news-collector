package ru.petrelevich.news;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class BaseContainerTest {
    private static final Logger log = LoggerFactory.getLogger(BaseContainerTest.class);
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>("postgres:17.6");

    private static final GenericContainer<?> NEWS_SOURCE = new GenericContainer<>(
            "registry.gitlab.com/petrelevich/dockerregistry/test-news-source:0.0.0-0.8fd1a7d2.dirty-SNAPSHOT");
    private static final int NEWS_SOURCE_PORT = 7070;

    static {
        POSTGRE_SQL_CONTAINER.start();
        NEWS_SOURCE.addExposedPort(NEWS_SOURCE_PORT);
        NEWS_SOURCE.start();
    }

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        log.info("POSTGRE_SQL_CONTAINER.getJdbcUrl:{}", POSTGRE_SQL_CONTAINER.getJdbcUrl());
        registry.add("data-source.url", () -> POSTGRE_SQL_CONTAINER.getJdbcUrl() + "&stringtype=unspecified");
        registry.add("data-source.user", POSTGRE_SQL_CONTAINER::getUsername);
        registry.add("data-source.pwd", POSTGRE_SQL_CONTAINER::getPassword);

        var testSourcePort = getNewsSourcePort();
        log.info("testSourcePort:{}", testSourcePort);
        registry.add("feeders.nrd.url:{}", () -> String.format("http://localhost:%d/test-nrd", testSourcePort));
    }

    public static int getNewsSourcePort() {
        return NEWS_SOURCE.getMappedPort(NEWS_SOURCE_PORT);
    }
}
