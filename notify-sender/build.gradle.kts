dependencies {
    implementation("ch.qos.logback:logback-classic")

    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    implementation(project(":common"))
}