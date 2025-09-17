package ru.petrelevich.news;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.petrelevich.news.model.News;

public class NewsSaverPostgres implements NewsSaver {
    private static final Logger log = LoggerFactory.getLogger(NewsSaverPostgres.class);
    private final DataSource dataSource;
    private final DateTimeProvider dateTimeProvider;
    private static final String INSERT_SQL =
            """
            insert into news(news_link, news_date, news_title, news_text, news_isin, created_at, created_at_day)
                values (?,?,?,?,?,?,?)
            ON CONFLICT (news_link) DO NOTHING
            """;

    public NewsSaverPostgres(DataSource dataSource, DateTimeProvider dateTimeProvider) {
        this.dataSource = dataSource;
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public boolean save(News news) {
        var insertResult = false;
        try {
            try (var connection = dataSource.getConnection()) {
                try (var ps = connection.prepareStatement(INSERT_SQL)) {
                    var idx = 1;
                    ps.setString(idx++, news.link());
                    ps.setTimestamp(idx++, ts(news.date()));
                    ps.setString(idx++, news.title());
                    ps.setString(idx++, news.text());
                    ps.setString(idx++, news.isin());
                    ps.setTimestamp(idx++, ts(dateTimeProvider.now()));
                    ps.setDate(idx, tsDay(dateTimeProvider.now()));

                    var rowCount = ps.executeUpdate();
                    if (rowCount != 0) {
                        insertResult = true;
                        log.info("saved news, title:{}", news.title());
                    }
                }
                connection.commit();
            }
        } catch (Exception ex) {
            log.error("save exception, news:{}", news, ex);
        }
        return insertResult;
    }

    private java.sql.Timestamp ts(LocalDateTime ldt) {
        return Timestamp.valueOf(ldt);
    }

    private java.sql.Date tsDay(LocalDateTime ldt) {
        return Date.valueOf(ldt.toLocalDate());
    }
}
