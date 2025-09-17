package ru.petrelevich.news;

import ru.petrelevich.news.model.Note;

public interface NoteSender {
    void send(Note msg);
}
