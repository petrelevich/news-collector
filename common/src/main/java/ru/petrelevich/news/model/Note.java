package ru.petrelevich.news.model;

import java.time.LocalDateTime;

public record Note(LocalDateTime date, String title, String text, String link) {}
