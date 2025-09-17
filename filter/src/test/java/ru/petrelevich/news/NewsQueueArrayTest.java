package ru.petrelevich.news;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import ru.petrelevich.news.model.News;

class NewsQueueArrayTest {

    @Test
    void testPutTake() {
        // given
        int size = 10;
        var filterOut = List.of("linkOut1", "linkOut2", "linkOut3");
        var newsFilter = new NewsFilterCaffeine(filterOut);
        var newsQueue = new NewsQueueArray(newsFilter, size);
        var news1 = new News("RU000A10BFM0", LocalDateTime.now(), "t1", "news1", "link1");
        var news2 = new News("RU000A10CRB6", LocalDateTime.now(), "t2", "news2", "link2");
        var news3 = new News("RU000A10BFM0", LocalDateTime.now(), "t3", "news3", "link3");

        // when
        var res1 = newsQueue.put(news1);
        var res2 = newsQueue.put(news2);
        var res3 = newsQueue.put(news3);

        // then
        assertThat(res1).isTrue();
        assertThat(res2).isTrue();
        assertThat(res3).isTrue();

        assertThat(newsQueue.take()).isEqualTo(news1);
        assertThat(newsQueue.take()).isEqualTo(news2);
        assertThat(newsQueue.take()).isEqualTo(news3);
        assertThat(newsQueue.getSize()).isZero();
    }

    @Test
    void testPutTakeFilter() {
        // given
        int size = 10;
        var filterOut = List.of("linkOut1", "linkOut2", "linkOut3");
        var newsFilter = new NewsFilterCaffeine(filterOut);
        var newsQueue = new NewsQueueArray(newsFilter, size);
        var linkDuplicated = "link_dup";
        var news1 = new News("RU000A10BFM0", LocalDateTime.now(), "t1", "news1", linkDuplicated);
        var news2 = new News("RU000A10CRB6", LocalDateTime.now(), "t2", "news2", linkDuplicated);
        var news3 = new News("RU000A10CRB6", LocalDateTime.now(), "t3", "news3", "link3");

        // when
        var res1 = newsQueue.put(news1);
        var res2 = newsQueue.put(news2);
        var res3 = newsQueue.put(news3);

        // then
        assertThat(res1).isTrue();
        assertThat(res2).isFalse();
        assertThat(res3).isTrue();

        assertThat(newsQueue.take()).isEqualTo(news1);
        assertThat(newsQueue.take()).isEqualTo(news3);
        assertThat(newsQueue.getSize()).isZero();
    }
}
