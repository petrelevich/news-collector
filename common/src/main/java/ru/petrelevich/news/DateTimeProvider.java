package ru.petrelevich.news;

import java.time.LocalDateTime;

public interface DateTimeProvider {
    LocalDateTime now();
}
