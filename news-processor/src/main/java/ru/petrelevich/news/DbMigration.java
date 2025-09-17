package ru.petrelevich.news;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbMigration {
    private static final Logger log = LoggerFactory.getLogger(DbMigration.class);

    private DbMigration() {}

    public static void doMigration(DataSource dataSource) {
        log.info("db migration started...");
        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:/db/migration")
                .load();
        flyway.migrate();
        log.info("db migration finished.");
    }
}
