package ru.petrelevich.news.api;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.petrelevich.news.BaseContainerTest;
import ru.petrelevich.news.NewsHttpClient;
import ru.petrelevich.news.NoteSender;
import ru.petrelevich.news.NoteSenderTester;
import ru.petrelevich.news.TestConfig;
import ru.petrelevich.news.model.News;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
class ApiFeedersTest extends BaseContainerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private NoteSender noteSenderTest;

    @Test
    void testExec() throws Exception {
        // given
        var expectedNewsListSize = 4;
        var expectedNews1 = new News(
                "RU000A103UL3",
                LocalDateTime.of(2025, 9, 15, 16, 45, 11),
                "(INTR) О корпоративном действии \"Выплата купонного дохода\" с ценными бумагами эмитента ООО \"Татнефтехим\" ИНН 1655201119 (облигация 4B02-01-00017-L-001P / ISIN RU000A103UL3)",
                "Реквизиты корпоративного действия Референс корпоративного действия 635624 Код типа корпоративного действия INTR Тип корпоративного действия Выплата купонного дохода Дата КД (план.) 19 ноября 2025 г. Дата КД (расч.) 19 ноября 2025 г. Дата фиксации (по решению о выпуске) 18 ноября 2025 г. Информация о ценных бумагах Эмитент Регистрационный номер Дата регистрации Категория Депозитарный код выпуска ISIN Номинальная стоимость Остаточная номинальная...",
                "http://nsddata.ru/ru/news/view/1303413");
        var expectedNews2 = new News(
                "RU000A10CRB6",
                LocalDateTime.of(2025, 9, 15, 16, 19, 3),
                "(INTR) О корпоративном действии \"Выплата купонного дохода\" с ценными бумагами эмитента ПАО \"Кокс\" ИНН 4205001274 (облигация 4B02-06-10799-F-001P / ISIN RU000A10CRB6)",
                "Реквизиты корпоративного действия Референс корпоративного действия 1081445 Код типа корпоративного действия INTR Тип корпоративного действия Выплата купонного дохода Дата КД (план.) 16 октября 2025 г. Дата КД (расч.) 16 октября 2025 г. Дата фиксации (по решению о выпуске) 15 октября 2025 г. Информация о ценных бумагах Эмитент Регистрационный номер Дата регистрации Категория Депозитарный код выпуска ISIN Номинальная стоимость Остаточная номинальная...",
                "http://nsddata.ru/ru/news/view/1303402");

        // when
        var result1 =
                mockMvc.perform(get("/api/v1/newsConsumers/exec")).andReturn().getResponse();
        // then
        assertThat(result1.getStatus()).isEqualTo(SC_OK);
        assertThat(result1.getContentAsString()).isNotEmpty();

        // switchToFile-2
        try (var executor = Executors.newSingleThreadExecutor()) {
            var httpFeed = new NewsHttpClient(
                    executor, String.format("http://localhost:%d/set-test-nrd-2", getNewsSourcePort()), true);
            var switchResult = httpFeed.doPut();
            assertThat(switchResult).isNotEmpty();
            assertThat(switchResult.get().status()).isEqualTo(200);
        }

        // when
        var result2 =
                mockMvc.perform(get("/api/v1/newsConsumers/exec")).andReturn().getResponse();
        // then
        assertThat(result2.getStatus()).isEqualTo(SC_OK);
        assertThat(result2.getContentAsString()).isEqualTo("newsConsumer:FeederNrd. returned:2");

        // check database
        var newsList = new CopyOnWriteArrayList<News>();

        await().atMost(Duration.ofSeconds(10)).until(() -> {
            try (var connection = dataSource.getConnection();
                    var ps = connection.prepareStatement("select * from news");
                    var rs = ps.executeQuery()) {
                while (rs.next()) {
                    var news = new News(
                            rs.getString("news_isin"),
                            ts(rs.getTimestamp("news_date")),
                            rs.getString("news_title"),
                            rs.getString("news_text"),
                            rs.getString("news_link"));
                    newsList.add(news);
                }
            }
            return newsList.size() == expectedNewsListSize;
        });

        assertThat(newsList).contains(expectedNews1, expectedNews2);

        // verify note
        var noteLog = ((NoteSenderTester) noteSenderTest).getNoteLog();
        assertThat(noteLog).hasSize(expectedNewsListSize);
    }

    private LocalDateTime ts(java.sql.Timestamp ts) {
        return ts.toLocalDateTime();
    }
}
