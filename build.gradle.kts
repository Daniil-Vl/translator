plugins {
    java
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "ru.tbank"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

//configurations {
//    all*.exclude group: 'org.springframework.boot', module: 'spring-boot-starter-logging'
//    all*.exclude group: 'ch.qos.logback', module: 'logback-classic'
//    all*.exclude group: 'ch.qos.logback', module: 'logback-core'
//}

//configurations.forEach {
//    it.exclude("org.springframework.boot", "spring-boot-starter-logging")
//}

// Groovy
//configurations {
//    all*.exclude group: 'com.google.guava', module: 'listenablefuture'
//}

// Kotlin
//configurations.forEach {
//    it.exclude("com.google.guava", "listenablefuture")
//    it.exclude(group = "org.jetbrains", module = "annotations")
//}


dependencies {
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.liquibase:liquibase-core")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.assertj:assertj-core:3.11.1")

    testImplementation("org.wiremock:wiremock-standalone:3.3.1")

    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.springframework.boot:spring-boot-starter-log4j2")
    modules {
        module("org.springframework.boot:spring-boot-starter-logging") {
            replacedBy("org.springframework.boot:spring-boot-starter-log4j2", "Use Log4j2 instead of Logback")
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
