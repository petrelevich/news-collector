package ru.petrelevich.news;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import ru.petrelevich.news.model.News;

class ParserNrdTest {

    @Test
    void parse() throws URISyntaxException, IOException {
        // given
        var uriTestContent = ClassLoader.getSystemResource("nrd.xml").toURI();
        var pathTestContent = Paths.get(uriTestContent);
        var content = Files.readString(pathTestContent);
        var parser = new ParserNrd();

        var news1 = new News(
                "XS2370039187",
                LocalDateTime.of(2025, 9, 13, 8, 51, 5),
                "(INTR) О корпоративном действии \"Выплата купонного дохода\" - GOLDMAN SACHS INT 23/09/26 (облигация ISIN XS2370039187)",
                "Реквизиты корпоративного действия Референс корпоративного действия 1052667 Код типа корпоративного действия INTR Тип корпоративного действия Выплата купонного дохода Признак обязательности КД MAND Обязательное событие, инструкций не требуется Дата КД (план.) 22 сентября 2025 г. Дата КД (расч.) 22 сентября 2025 г. Дата фиксации 19 сентября 2025 г. Информация о ценных бумагах Наименование ценной бумаги Категория Депозитарный код выпуска ISIN Номинальная...",
                "http://nsddata.ru/ru/news/view/1303053");

        var news2 = new News(
                "XS2294418681",
                LocalDateTime.of(2025, 9, 13, 8, 34, 18),
                "(INTR) О корпоративном действии \"Выплата купонного дохода\" - Goldman Sachs International 23/03/26 (облигация ISIN XS2294418681)",
                "Реквизиты корпоративного действия Референс корпоративного действия 1052981 Код типа корпоративного действия INTR Тип корпоративного действия Выплата купонного дохода Признак обязательности КД MAND Обязательное событие, инструкций не требуется Дата КД (план.) 22 сентября 2025 г. Дата КД (расч.) 22 сентября 2025 г. Дата фиксации 19 сентября 2025 г. Информация о ценных бумагах Наименование ценной бумаги Категория Депозитарный код выпуска ISIN Номинальная...",
                "http://nsddata.ru/ru/news/view/1303043");

        // when
        var news = parser.parse(content);

        // then
        assertThat(news).hasSize(10).contains(news1, news2);
    }
}
