package ru.petrelevich.news;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import ru.petrelevich.news.model.Note;

@SuppressWarnings("java:S2187")
public class NoteSenderTester implements NoteSender {
    private final List<Note> noteLog = new CopyOnWriteArrayList<>();

    @Override
    public void send(Note msg) {
        noteLog.add(msg);
    }

    public List<Note> getNoteLog() {
        return noteLog;
    }
}
