package ru.petrelevich.news;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.petrelevich.news.dto.Rss;
import ru.petrelevich.news.model.News;

public class ParserNrd implements Parser {
    private static final Logger log = LoggerFactory.getLogger(ParserNrd.class);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final XmlMapper xmlMapper = new XmlMapper();
    private final IsinExtractor isinExtractor = new IsinExtractor();

    @Override
    public List<News> parse(String content) {
        try {
            var rss = xmlMapper.readValue(content, Rss.class);
            var newsList = new ArrayList<News>(rss.channel().item().size());
            for (var item : rss.channel().item()) {
                newsList.add(new News(
                        isinExtractor.extract(item.title()),
                        LocalDateTime.parse(item.pubDate(), formatter),
                        item.title(),
                        item.description(),
                        item.link()));
            }
            log.info("parsed newsList:{}", newsList.size());
            return newsList;
        } catch (JsonProcessingException e) {
            log.error("news parsing error, content:{}", content, e);
            return Collections.emptyList();
        }
    }
}
