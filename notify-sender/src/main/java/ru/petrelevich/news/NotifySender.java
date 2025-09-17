package ru.petrelevich.news;

import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotifySender {
    private static final Logger log = LoggerFactory.getLogger(NotifySender.class);

    private final NoteQueue noteQueue;
    private final NoteSender noteSender;
    private final ExecutorService executor;

    public NotifySender(NoteQueue noteQueue, NoteSender noteSender, ExecutorService executor) {
        this.noteQueue = noteQueue;
        this.noteSender = noteSender;
        this.executor = executor;
    }

    public void scheduleStart() {
        executor.submit(this::takeNote);
    }

    private void takeNote() {
        log.info("takeNote started");
        while (!Thread.currentThread().isInterrupted()) {
            var note = noteQueue.take();
            if (note != null) {
                noteSender.send(note);
            } else {
                log.info("noteQueue returned nothing");
            }
        }
        log.info("takeNote finished");
    }
}
