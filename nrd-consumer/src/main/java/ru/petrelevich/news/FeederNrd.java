package ru.petrelevich.news;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.petrelevich.news.model.News;

public class FeederNrd implements Feeder {
    private static final Logger log = LoggerFactory.getLogger(FeederNrd.class);

    private final NewsHttpClient newsHttpClient;
    private final Parser parserNrd;

    public FeederNrd(Executor executor, Parser parserNrd, String url) {
        log.info("FeederNrd, url:{}", url);
        this.newsHttpClient = new NewsHttpClient(executor, url, false);
        this.parserNrd = parserNrd;
        log.info("FeederNrd created");
    }

    @Override
    public String getName() {
        return "FeederNrd";
    }

    @Override
    public List<News> getNews() {
        var response = newsHttpClient.doGet();
        if (response.isEmpty()) {
            log.info("FeederNrd returned empty");
            return Collections.emptyList();
        }
        if (response.get().status() != 200) {
            log.info("FeederNrd returned status:{}", response.get().status());
            return Collections.emptyList();
        }
        var content = response.get().content();
        log.info("FeederNrd returned content.length:{}", content.length());
        return parserNrd.parse(content);
    }
}
