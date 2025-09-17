package ru.petrelevich.news;

import ru.petrelevich.news.model.Note;

public interface NoteQueue {
    boolean put(Note note);

    Note take();

    int getSize();
}
