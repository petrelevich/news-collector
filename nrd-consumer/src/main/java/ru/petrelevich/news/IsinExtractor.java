package ru.petrelevich.news;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IsinExtractor {
    private static final Logger log = LoggerFactory.getLogger(IsinExtractor.class);

    private final Pattern pattern = Pattern.compile("\\s([A-Z0-9]{12})\\s");

    public String extract(String text) {
        String isin = null;
        if (text != null) {
            try {
                var textNormalized = text.replace(")", " ").replace(",", " ").replace(".", " ");
                Matcher matcher = pattern.matcher(textNormalized);
                if (matcher.find()) {
                    isin = matcher.group(1);
                }
            } catch (Exception ex) {
                log.error("error, text:{}", text, ex);
            }
        }
        return isin;
    }
}
