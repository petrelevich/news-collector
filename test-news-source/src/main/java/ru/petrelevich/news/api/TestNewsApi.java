package ru.petrelevich.news.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestNewsApi {
    private static final Logger log = LoggerFactory.getLogger(TestNewsApi.class);

    private final AtomicReference<String> currentNrdFileName = new AtomicReference<>("nrd-1.xml");

    @PutMapping("/set-test-nrd-1")
    public String setTestNrd1() {
        currentNrdFileName.set("nrd-1.xml");
        return "Ok";
    }

    @PutMapping("/set-test-nrd-2")
    public String setTestNrd2() {
        currentNrdFileName.set("nrd-2.xml");
        return "Ok";
    }

    @GetMapping("/test-nrd")
    public String testNrd() throws URISyntaxException, IOException {
        var fileName = currentNrdFileName.get();
        var uriTestContent = ClassLoader.getSystemResource(fileName).toURI();
        var pathTestContent = Paths.get(uriTestContent);
        var content = Files.readString(pathTestContent);
        log.info("return content file:{}", fileName);
        return content;
    }
}
