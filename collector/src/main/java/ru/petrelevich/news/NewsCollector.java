package ru.petrelevich.news;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// http://localhost:8080/actuator/health
//

@SpringBootApplication
public class NewsCollector {

    public static void main(String[] args) {
        SpringApplication.run(NewsCollector.class, args);
    }
}
