package ru.petrelevich.news;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewsHttpClient {
    private static final String HEADER_KEEP_ALIVE = "Keep-Alive";
    private static final Logger log = LoggerFactory.getLogger(NewsHttpClient.class);
    private final Executor executor;
    private final java.net.http.HttpClient httpClient;
    private final String url;
    private final boolean keepAlive;

    public NewsHttpClient(Executor executor, String url, boolean keepAlive) {
        this.executor = executor;
        this.httpClient = makeHttpClient();
        this.url = url;
        this.keepAlive = keepAlive;
    }

    public Optional<HttpFeedResponse> doGet() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .header(HEADER_KEEP_ALIVE, Boolean.toString(keepAlive))
                .GET()
                .build();
        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return Optional.of(new HttpFeedResponse(response.statusCode(), response.body()));
        } catch (Exception ex) {
            log.error("doGet error, url:{}", url, ex);
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
        return Optional.empty();
    }

    public Optional<HttpFeedResponse> doPut() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .header(HEADER_KEEP_ALIVE, Boolean.toString(keepAlive))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return Optional.of(new HttpFeedResponse(response.statusCode(), response.body()));
        } catch (Exception ex) {
            log.error("doPut error, url:{}", url, ex);
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
        return Optional.empty();
    }

    public Optional<HttpFeedResponse> doPost(String message) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json")
                .header(HEADER_KEEP_ALIVE, Boolean.toString(keepAlive))
                .POST(HttpRequest.BodyPublishers.ofString(message))
                .build();
        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return Optional.of(new HttpFeedResponse(response.statusCode(), response.body()));
        } catch (Exception ex) {
            log.error("doPost error", ex);
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
        return Optional.empty();
    }

    private java.net.http.HttpClient makeHttpClient() {
        System.setProperty("jdk.httpclient.disableRetryConnect", "true");
        System.setProperty("jdk.httpclient.enableAllMethodRetry", "false");
        System.setProperty("jdk.httpclient.redirects.retrylimit", "1");
        System.setProperty("jdk.httpclient.HttpClient.log", "errors"); // all

        return java.net.http.HttpClient.newBuilder()
                .version(java.net.http.HttpClient.Version.HTTP_1_1)
                .followRedirects(java.net.http.HttpClient.Redirect.NEVER)
                .executor(executor)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }
}
