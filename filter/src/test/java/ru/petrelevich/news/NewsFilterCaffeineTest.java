package ru.petrelevich.news;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import ru.petrelevich.news.model.News;

class NewsFilterCaffeineTest {

    @Test
    void testFilterTrue() {
        // given
        var filterOutName = "filterOutName";
        var filterOutNamePrefix = "fixfilterOutName";
        var filterOut = List.of("linkOut1", "linkOut2", "linkOut3", filterOutName);
        var newsFilter = new NewsFilterCaffeine(filterOut);

        var linkDuplicated = "link_dup";
        var news1 = new News("RU000A10BFM0", LocalDateTime.now(), "t1", "news1", "link1");
        var news2 = new News("RU000A10CRB6", LocalDateTime.now(), "t2", "news2", "link2");
        var news3 = new News("RU000A103UL3", LocalDateTime.now(), "t3", "news3", "link3");

        var news4 = new News("RU000A10CRC4", LocalDateTime.now(), "t4", "news4", linkDuplicated);
        var news5 = new News("RU000A10BFM0", LocalDateTime.now(), "t5", "news5", linkDuplicated);
        var news6 = new News("RU000A107E65", LocalDateTime.now(), filterOutName, "news6", "link6");
        var news7 = new News("RU000A106M82", LocalDateTime.now(), filterOutNamePrefix, "news7", "link7");
        var news8 = new News("USP7721BAE13", LocalDateTime.now(), "t8", "news8", "link8");

        // when-then
        assertThat(newsFilter.filter(news1)).isTrue();
        assertThat(newsFilter.filter(news2)).isTrue();
        assertThat(newsFilter.filter(news3)).isTrue();
        assertThat(newsFilter.filter(news3)).isFalse();

        // then
        assertThat(newsFilter.filter(news4)).isTrue();
        assertThat(newsFilter.filter(news5)).isFalse();
        assertThat(newsFilter.filter(news6)).isFalse();
        assertThat(newsFilter.filter(news7)).isTrue();
        assertThat(newsFilter.filter(news8)).isFalse();
    }
}
