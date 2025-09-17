package ru.petrelevich.news;

public class HttpFeedException extends RuntimeException {
    public HttpFeedException(String message) {
        super(message);
    }
}
