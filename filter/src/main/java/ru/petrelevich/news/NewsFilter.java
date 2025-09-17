package ru.petrelevich.news;

import ru.petrelevich.news.model.News;

public interface NewsFilter {
    boolean filter(News news);
}
