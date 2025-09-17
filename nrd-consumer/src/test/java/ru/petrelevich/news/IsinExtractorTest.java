package ru.petrelevich.news;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class IsinExtractorTest {

    @Test
    void extractTest() {
        // given
        var isinExtractor = new IsinExtractor();

        // then
        var result1 = isinExtractor.extract(
                "(INTR) (Выплата купонного дохода) О получении и передаче головным депозитарием, осуществляющим обязательное централизованное хранение ценных бумаг/централизованный учет прав на ценные бумаги, полученных им выплат по облигациям своим депонентам, которые являются номинальными держателями и управляющими, а также о размере выплаты, приходящейся на одну облигацию эмитента (ООО \"СОБИ-ЛИЗИНГ\", 2311127765, RU000A10BT00, 4B02-06-00632-R-001P)");
        // when
        assertThat(result1).isEqualTo("RU000A10BT00");

        // then
        var result2 = isinExtractor.extract(
                "(PRED) (Частичное погашение без уменьшения номинала) О получении головным депозитарием, осуществляющим обязательное централизованное хранение ценных бумаг/централизованный учет прав на ценные бумаги, подлежащих передаче выплат по облигациям эмитента (Государственная компания \"Автодор\", 7717151380, RU000A105NW0, 4-02-00011-T-003P)");
        // when
        assertThat(result2).isEqualTo("RU000A105NW0");

        // then
        var result3 = isinExtractor.extract(
                "(INTR) О корпоративном действии \"Выплата купонного дохода\" с ценными бумагами эмитента ПАО \"Кокс\" ИНН 4205001274 (облигация 4B02-06-10799-F-001P / ISIN RU000A10CRB6)");
        // when
        assertThat(result3).isEqualTo("RU000A10CRB6");

        // then
        var result4 = isinExtractor.extract(
                "(INTR) О корпоративном действии \"Выплата купонного дохода\" с ценными бумагами эмитента ПАО \"Кокс\" ИНН 4205001274 (облигация 4B02-06-10799-F-001P / ISIN RU000A10CRBFA6)");
        // when
        assertThat(result4).isNull();
    }
}
