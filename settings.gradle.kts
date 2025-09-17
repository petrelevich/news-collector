rootProject.name = "news-collector"

include("collector")
include("common")
include("nrd-consumer")
include("filter")
include("news-processor")
include("notify-sender")

include("test-news-source")

pluginManagement {
    plugins {
        id("fr.brouillard.oss.gradle.jgitver") version "0.10.0-rc03"
        id("io.spring.dependency-management") version "1.1.7"
        id("org.springframework.boot") version "3.5.5"
        id("com.google.cloud.tools.jib") version "3.4.5"
        id("name.remal.sonarlint") version "6.0.0-rc-2"
        id("com.diffplug.spotless") version "6.25.0"
    }
}
