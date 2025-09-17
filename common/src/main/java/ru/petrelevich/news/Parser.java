package ru.petrelevich.news;

import java.util.List;
import ru.petrelevich.news.model.News;

public interface Parser {
    List<News> parse(String content);
}
