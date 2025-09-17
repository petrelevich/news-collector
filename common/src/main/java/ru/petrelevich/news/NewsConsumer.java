package ru.petrelevich.news;

public interface NewsConsumer {

    void scheduleStart();

    String getName();

    int consume();
}
