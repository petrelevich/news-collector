package ru.petrelevich.news.model;

public record TelegramMessage(String parse_mode, String chat_id, String text) {}
