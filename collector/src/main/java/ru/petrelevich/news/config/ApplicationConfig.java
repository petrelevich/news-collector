package ru.petrelevich.news.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.petrelevich.news.DataSourceFactory;
import ru.petrelevich.news.DateTimeProvider;
import ru.petrelevich.news.DbMigration;
import ru.petrelevich.news.Feeder;
import ru.petrelevich.news.FeederNrd;
import ru.petrelevich.news.NewsConsumer;
import ru.petrelevich.news.NewsConsumerNrd;
import ru.petrelevich.news.NewsFilter;
import ru.petrelevich.news.NewsFilterCaffeine;
import ru.petrelevich.news.NewsQueue;
import ru.petrelevich.news.NewsQueueArray;
import ru.petrelevich.news.NewsSaver;
import ru.petrelevich.news.NewsSaverPostgres;
import ru.petrelevich.news.NoteQueue;
import ru.petrelevich.news.NoteQueueArray;
import ru.petrelevich.news.NoteSender;
import ru.petrelevich.news.NoteSenderTelegram;
import ru.petrelevich.news.NotifySender;
import ru.petrelevich.news.Parser;
import ru.petrelevich.news.ParserNrd;
import ru.petrelevich.news.Processor;

@Configuration
public class ApplicationConfig {
    private static final Logger log = LoggerFactory.getLogger(ApplicationConfig.class);

    @Bean
    public DateTimeProvider dateTimeProvider() {
        return LocalDateTime::now;
    }

    @Bean(name = "feedsExecutor", destroyMethod = "close")
    public ExecutorService feedsExecutor() {
        var factory = Thread.ofVirtual().name("feed-", 0).factory();
        return Executors.newThreadPerTaskExecutor(factory);
    }

    @Bean("parserNrd")
    public Parser parserNrd() {
        return new ParserNrd();
    }

    @Bean("feederNrd")
    public Feeder feederNrd(
            @Qualifier("feedsExecutor") ExecutorService feedsExecutor,
            @Qualifier("parserNrd") Parser parserNrd,
            @Value("${feeders.nrd.url}") String url) {
        return new FeederNrd(feedsExecutor, parserNrd, url);
    }

    @Bean
    public NewsFilter newsFilterCaffeine() {
        return new NewsFilterCaffeine(readFilterOutFile());
    }

    @Bean
    public NewsQueue newsQueueArray(NewsFilter newsFilter) {
        int queueSize = 10_000;
        return new NewsQueueArray(newsFilter, queueSize);
    }

    @Bean(value = "newsTransferExecutor", destroyMethod = "shutdownNow")
    public ScheduledExecutorService newsTransferExecutor() {
        ThreadFactory threadFactory =
                task -> Thread.ofPlatform().name("newsTransfer").unstarted(task);
        return Executors.newSingleThreadScheduledExecutor(threadFactory);
    }

    @Bean
    public NewsConsumer newsTransferNrd(
            @Qualifier("feederNrd") Feeder feederNrd,
            @Qualifier("newsTransferExecutor") ScheduledExecutorService newsTransfer,
            NewsQueue queue) {
        Duration execPeriod = Duration.ofMinutes(5);
        var newsConsumer = new NewsConsumerNrd(feederNrd, queue, newsTransfer, execPeriod);
        newsConsumer.scheduleStart();
        return newsConsumer;
    }

    @Bean
    public DataSource dataSource(
            @Value("${data-source.url}") String url,
            @Value("${data-source.user}") String user,
            @Value("${data-source.pwd}") String pwd) {
        var ds = new DataSourceFactory().make(url, user, pwd);
        DbMigration.doMigration(ds);
        return ds;
    }

    @Bean
    public NewsSaver newsSaver(DataSource dataSource, DateTimeProvider dateTimeProvider) {
        return new NewsSaverPostgres(dataSource, dateTimeProvider);
    }

    @Bean(name = "processorExecutor", destroyMethod = "shutdownNow")
    public ExecutorService processorExecutor() {
        var factory = Thread.ofVirtual().name("processor").factory();
        return Executors.newSingleThreadExecutor(factory);
    }

    @Bean
    public Processor processor(
            @Qualifier("processorExecutor") ExecutorService processorExecutor,
            NewsQueue newsQueue,
            NewsSaver newsSaver,
            NoteQueue noteQueue) {
        var processor = new Processor(processorExecutor, newsQueue, newsSaver, noteQueue);
        processor.scheduleStart();
        return processor;
    }

    @Bean
    public TelegramConfig telegramConfig(
            @Value("${telegram.host}") String host,
            @Value("${telegram.token-file}") String tokenFile,
            @Value("${telegram.chat-id}") String chatId) {
        String token;
        try {
            token = Files.readString(Path.of(tokenFile)).trim();
        } catch (IOException e) {
            log.error("reading token file error:{}", tokenFile, e);
            throw new IllegalStateException("Reading Telegram token file has error");
        }
        return new TelegramConfig(host, chatId, token);
    }

    @Bean(name = "telegramExecutor", destroyMethod = "close")
    public ExecutorService telegramExecutor() {
        var factory = Thread.ofVirtual().name("telegram").factory();
        return Executors.newSingleThreadExecutor(factory);
    }

    @Bean
    public NoteSender noteSender(
            @Qualifier("telegramExecutor") Executor executor,
            ObjectMapper objectMapper,
            TelegramConfig telegramConfig) {
        return new NoteSenderTelegram(executor, objectMapper, telegramConfig);
    }

    @Bean(value = "notifyExecutor", destroyMethod = "shutdownNow")
    public ScheduledExecutorService notifyExecutor() {
        ThreadFactory threadFactory = task -> Thread.ofVirtual().name("notify").unstarted(task);
        return Executors.newSingleThreadScheduledExecutor(threadFactory);
    }

    @Bean
    public NoteQueue noteQueue() {
        return new NoteQueueArray(1_000);
    }

    @Bean
    public NotifySender notifySender(
            @Qualifier("notifyExecutor") ExecutorService executor, NoteQueue noteQueue, NoteSender noteSender) {
        var notifySender = new NotifySender(noteQueue, noteSender, executor);
        notifySender.scheduleStart();
        return notifySender;
    }

    private List<String> readFilterOutFile() {
        try {
            var uriTestContent =
                    ClassLoader.getSystemResource("filter-out.list").toURI();
            var pathTestContent = Paths.get(uriTestContent);
            var content = Files.readString(pathTestContent);
            return List.of(content.split(","));
        } catch (Exception ex) {
            log.error("can't read filterOut file");
        }
        return Collections.emptyList();
    }
}
