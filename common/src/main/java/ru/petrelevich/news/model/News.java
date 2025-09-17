package ru.petrelevich.news.model;

import java.time.LocalDateTime;

public record News(String isin, LocalDateTime date, String title, String text, String link) {}
