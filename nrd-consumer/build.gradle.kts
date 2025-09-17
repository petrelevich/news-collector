dependencies {
    implementation("ch.qos.logback:logback-classic")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
    implementation(project(":common"))

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation ("org.assertj:assertj-core")
}

