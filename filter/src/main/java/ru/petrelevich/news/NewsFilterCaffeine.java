package ru.petrelevich.news;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.petrelevich.news.model.News;

public class NewsFilterCaffeine implements NewsFilter {
    private static final Logger log = LoggerFactory.getLogger(NewsFilterCaffeine.class);
    private static final Duration STORAG_DURATION = Duration.ofMinutes(60);
    private static final int STORAGE_SIZE = 1_000;
    private final Cache<@NotNull String, String> newsCache;
    private final List<String> filterOut;

    public NewsFilterCaffeine(List<String> filterOut) {
        log.info("filterOut size:{}", filterOut.size());
        this.filterOut = filterOut;
        this.newsCache = Caffeine.newBuilder()
                .expireAfterWrite(STORAG_DURATION.toMinutes(), TimeUnit.MINUTES)
                .maximumSize(STORAGE_SIZE)
                .removalListener((String link, String title, RemovalCause cause) ->
                        log.debug("link:{}, title:{}. removed, cause:{}", link, title, cause))
                .build();
    }

    @Override
    public boolean filter(News news) {
        if (!ruIsinFilter(news)) {
            return false;
        }

        if (existsFilterOut(news)) {
            return false;
        }

        if (newsCache.getIfPresent(news.link()) == null) {
            newsCache.put(news.link(), news.title());
            return true;
        }
        return false;
    }

    private boolean ruIsinFilter(News news) {
        if (news.isin() == null) {
            return false;
        }
        return news.isin().startsWith("RU");
    }

    private boolean existsFilterOut(News news) {
        var titleSet = new HashSet<>();
        for (var word : news.title().replace(")", " ").split(" ")) {
            if (word.length() > 5) {
                titleSet.add(word);
            }
        }
        for (var word : filterOut) {
            if (titleSet.contains(word)) {
                return true;
            }
        }
        return false;
    }
}
