package ru.petrelevich.news;

import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.petrelevich.news.model.News;
import ru.petrelevich.news.model.Note;

public class Processor {
    private static final Logger log = LoggerFactory.getLogger(Processor.class);
    private final NewsQueue newsQueue;
    private final NewsSaver newsSaver;
    private final ExecutorService executor;
    private final NoteQueue noteQueue;

    public Processor(ExecutorService executor, NewsQueue newsQueue, NewsSaver newsSaver, NoteQueue noteQueue) {
        this.newsQueue = newsQueue;
        this.noteQueue = noteQueue;
        this.newsSaver = newsSaver;
        this.executor = executor;
    }

    public void scheduleStart() {
        executor.submit(this::process);
    }

    private void process() {
        log.info("processing started");
        while (!Thread.currentThread().isInterrupted()) {
            var news = newsQueue.take();
            if (news != null) {
                var saved = newsSaver.save(news);
                if (saved) {
                    noteQueue.put(makeNote(news));
                }
            } else {
                log.info("newsQueue returned nothing");
            }
        }
        log.info("processing finished");
    }

    private Note makeNote(News news) {
        return new Note(news.date(), news.title(), news.text(), news.link());
    }
}
