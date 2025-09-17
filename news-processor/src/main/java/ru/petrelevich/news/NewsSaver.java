package ru.petrelevich.news;

import ru.petrelevich.news.model.News;

public interface NewsSaver {

    boolean save(News news);
}
