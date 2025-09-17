package ru.petrelevich.news.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record Item(
        @JacksonXmlProperty(localName = "title") String title,
        @JacksonXmlProperty(localName = "description") String description,
        @JacksonXmlProperty(localName = "link") String link,
        @JacksonXmlProperty(localName = "guid") String guid,
        @JacksonXmlProperty(localName = "pubDate") String pubDate) {}
