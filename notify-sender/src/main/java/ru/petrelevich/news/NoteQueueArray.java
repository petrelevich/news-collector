package ru.petrelevich.news;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.petrelevich.news.model.Note;

public class NoteQueueArray implements NoteQueue {
    private static final Logger log = LoggerFactory.getLogger(NoteQueueArray.class);
    private final ArrayBlockingQueue<Note> queue;

    public NoteQueueArray(int queueSize) {
        log.info("NotifyQueueArray, queueSize:{}", queueSize);
        queue = new ArrayBlockingQueue<>(queueSize);
    }

    @Override
    public boolean put(Note note) {
        try {
            if (queue.offer(note, 10, TimeUnit.MINUTES)) {
                return true;
            } else {
                log.warn("Note queue is full");
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        return false;
    }

    @Override
    public Note take() {
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
