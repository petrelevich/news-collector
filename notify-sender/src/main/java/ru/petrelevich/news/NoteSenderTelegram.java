package ru.petrelevich.news;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.petrelevich.news.config.TelegramConfig;
import ru.petrelevich.news.model.Note;
import ru.petrelevich.news.model.TelegramMessage;

public class NoteSenderTelegram implements NoteSender {
    private static final Logger log = LoggerFactory.getLogger(NoteSenderTelegram.class);

    private final NewsHttpClient newsHttpClient;
    private final ObjectMapper objectMapper;
    private final TelegramConfig telegramConfig;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public NoteSenderTelegram(Executor executor, ObjectMapper objectMapper, TelegramConfig telegramConfig) {
        String url = String.format("%s/bot%s/sendMessage", telegramConfig.host(), telegramConfig.token());
        this.newsHttpClient = new NewsHttpClient(executor, url, true);
        this.objectMapper = objectMapper;
        this.telegramConfig = telegramConfig;
    }

    @Override
    public void send(Note note) {
        String msg = "%s\n<b>%s</b>\n%s\n%s";

        var telegramMessage = new TelegramMessage(
                "HTML",
                telegramConfig.chatId(),
                String.format(msg, formatter.format(note.date()), note.title(), note.text(), note.link()));
        try {
            var result = newsHttpClient.doPost(objectMapper.writeValueAsString(telegramMessage));
            if (result.isEmpty()) {
                log.error("Send with error");
            } else {
                if (result.get().status() != 200) {
                    log.error(
                            "Send with error, code:{}. content:{}",
                            result.get().status(),
                            result.get().content());
                }
            }
        } catch (Exception ex) {
            log.error("Send to Telegram error, msg:{}", msg, ex);
        }
    }
}
