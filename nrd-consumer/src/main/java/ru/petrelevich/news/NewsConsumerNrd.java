package ru.petrelevich.news;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewsConsumerNrd implements NewsConsumer {
    private static final Logger log = LoggerFactory.getLogger(NewsConsumerNrd.class);

    private final NewsQueue queue;
    private final Feeder feeder;
    private final ScheduledExecutorService executor;
    private final Duration execPeriod;

    public NewsConsumerNrd(Feeder feeder, NewsQueue queue, ScheduledExecutorService executor, Duration execPeriod) {
        this.queue = queue;
        this.feeder = feeder;
        this.executor = executor;
        this.execPeriod = execPeriod;
    }

    @Override
    public void scheduleStart() {
        executor.scheduleAtFixedRate(this::consume, 0, execPeriod.toMinutes(), TimeUnit.MINUTES);
        log.info("consume scheduled");
    }

    @Override
    public String getName() {
        return feeder.getName();
    }

    @Override
    public int consume() {
        var newsList = feeder.getNews();
        int newCounter = 0;
        for (var news : newsList) {
            if (queue.put(news)) {
                newCounter++;
            }
        }
        log.info("transferTo all:{}, new:{}", newsList.size(), newCounter);
        return newCounter;
    }
}
