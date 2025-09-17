package ru.petrelevich.news;

import ru.petrelevich.news.model.News;

public interface NewsQueue {
    boolean put(News news);

    News take();

    int getSize();
}
