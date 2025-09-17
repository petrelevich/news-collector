package ru.petrelevich.news.api;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.petrelevich.news.NewsConsumer;

@RestController
@RequestMapping("${application.rest.api.prefix}/v1/newsConsumers")
public class ApiNewsConsumers {
    private static final Logger log = LoggerFactory.getLogger(ApiNewsConsumers.class);
    private final List<NewsConsumer> newsConsumers;

    public ApiNewsConsumers(List<NewsConsumer> newsConsumers) {
        this.newsConsumers = newsConsumers;
    }

    // http://localhost:8080/api/v1/newsConsumers/exec
    @GetMapping("/exec")
    public String exec() {
        var report = new StringBuilder();
        for (var newsConsumer : newsConsumers) {
            var newCounter = newsConsumer.consume();
            log.info("newsConsumer:{}. newCounter:{}", newsConsumer.getName(), newCounter);
            report.append(String.format("newsConsumer:%s. returned:%d", newsConsumer.getName(), newCounter));
        }
        return report.toString();
    }
}
