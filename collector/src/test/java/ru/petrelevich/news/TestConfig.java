package ru.petrelevich.news;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public NoteSender noteSenderTest() {
        return new NoteSenderTester();
    }
}
