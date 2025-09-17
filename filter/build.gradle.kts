dependencies {
    implementation("ch.qos.logback:logback-classic")
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("org.jetbrains:annotations")
    implementation(project(":common"))

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation ("org.assertj:assertj-core")
}