package ru.petrelevich.news;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceFactory {
    private static final Logger log = LoggerFactory.getLogger(DataSourceFactory.class);

    public DataSource make(String url, String user, String pwd) {
        log.info("url:{}, user:{}, pwd:{}", url, user, (pwd == null ? "null" : "***"));

        var config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setConnectionTimeout(3000); // ms
        config.setIdleTimeout(60000); // ms
        config.setMaxLifetime(60000); // ms
        config.setAutoCommit(false);
        config.setMinimumIdle(3);
        config.setMaximumPoolSize(5);
        config.setPoolName("NewsHiPool");
        config.setRegisterMbeans(true);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        config.setUsername(user);
        config.setPassword(pwd);

        return new HikariDataSource(config);
    }
}
