package ru.petrelevich.news;

import java.util.List;
import ru.petrelevich.news.model.News;

public interface Feeder {
    String getName();

    List<News> getNews();
}
