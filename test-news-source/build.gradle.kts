plugins {
    id("org.springframework.boot")
    id ("com.google.cloud.tools.jib")
    id ("fr.brouillard.oss.gradle.jgitver")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
}

jib {
    container {
        creationTime.set("USE_CURRENT_TIMESTAMP")
    }
    from {
        image = "bellsoft/liberica-openjdk-alpine-musl:21.0.1"
    }

    to {
        image = "registry.gitlab.com/petrelevich/dockerregistry/test-news-source"
        tags = setOf(project.version.toString())
        auth {
            username = System.getenv("GITLAB_USERNAME")
            password = System.getenv("GITLAB_PASSWORD")
        }
    }
}

tasks {
    build {
        dependsOn(jibDockerBuild)
    }
}