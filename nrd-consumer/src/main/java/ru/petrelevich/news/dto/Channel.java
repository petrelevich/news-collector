package ru.petrelevich.news.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Channel(
        @JacksonXmlElementWrapper(useWrapping = false) @JacksonXmlProperty(localName = "item") List<Item> item) {}
