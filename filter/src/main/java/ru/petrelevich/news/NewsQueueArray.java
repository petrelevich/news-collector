package ru.petrelevich.news;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.petrelevich.news.model.News;

public class NewsQueueArray implements NewsQueue {
    private static final Logger log = LoggerFactory.getLogger(NewsQueueArray.class);
    private final ArrayBlockingQueue<News> queue;
    private final NewsFilter newsFilter;

    public NewsQueueArray(NewsFilter newsFilter, int queueSize) {
        log.info("NewsQueueArray, queueSize:{}", queueSize);
        this.newsFilter = newsFilter;
        queue = new ArrayBlockingQueue<>(queueSize);
    }

    @Override
    public boolean put(News news) {
        try {
            if (newsFilter.filter(news)) {
                if (queue.offer(news, 10, TimeUnit.MINUTES)) {
                    return true;
                } else {
                    log.warn("News queue is full");
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        return false;
    }

    @Override
    public News take() {
        try {
            return queue.poll(10, TimeUnit.MINUTES);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Override
    public int getSize() {
        return queue.size();
    }
}
