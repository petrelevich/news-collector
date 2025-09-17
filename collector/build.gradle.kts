plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation(project(":common"))
    implementation(project(":nrd-consumer"))
    implementation(project(":filter"))
    implementation(project(":news-processor"))
    implementation(project(":notify-sender"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.assertj:assertj-core")
    testImplementation ("org.testcontainers:testcontainers")
    testImplementation ("org.testcontainers:postgresql")
    testImplementation ("org.apache.commons:commons-compress")
    testImplementation("org.awaitility:awaitility")
}