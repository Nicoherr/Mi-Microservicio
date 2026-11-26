import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.5.16"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
    kotlin("plugin.jpa") version "1.9.23"
    jacoco
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

extra["tomcat.version"] = "10.1.36"
extra["spring-framework.version"] = "6.2.3"
extra["jackson.version"] = "2.18.2"
extra["logback.version"] = "1.5.16"

repositories {
    mavenCentral()
}

configurations.all {
    resolutionStrategy {
        force("org.apache.tomcat.embed:tomcat-embed-core:10.1.36")
        force("org.apache.tomcat.embed:tomcat-embed-websocket:10.1.36")
        force("org.springframework:spring-core:6.2.3")
        force("org.springframework:spring-web:6.2.3")
        force("org.springframework:spring-webmvc:6.2.3")
        force("org.springframework:spring-beans:6.2.3")
        force("org.springframework:spring-context:6.2.3")
        force("com.fasterxml.jackson.core:jackson-databind:2.18.2")
        force("com.fasterxml.jackson.core:jackson-core:2.18.2")
        force("com.fasterxml.jackson.core:jackson-annotations:2.18.2")
        force("ch.qos.logback:logback-classic:1.5.16")
        force("ch.qos.logback:logback-core:1.5.16")
        force("org.yaml:snakeyaml:2.3")
    }
}

dependencies {
    // Spring Boot Starters & BOM
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.5.16"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Metrics & Observability
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("net.logstash.logback:logstash-logback-encoder:8.0")

    // Database
    runtimeOnly("com.h2database:h2")

    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.13.12")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml"))
    }
}
