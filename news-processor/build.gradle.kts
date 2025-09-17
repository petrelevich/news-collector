dependencies {
    implementation("ch.qos.logback:logback-classic")
    implementation("org.flywaydb:flyway-core")
    implementation("com.zaxxer:HikariCP")
    implementation("org.postgresql:postgresql")
    implementation(project(":common"))
    implementation(project(":notify-sender"))

    runtimeOnly("org.flywaydb:flyway-database-postgresql")
}