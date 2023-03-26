plugins {
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.allopen") version "1.7.22"
    kotlin("plugin.noarg") version "1.7.22"
    id("io.swagger.core.v3.swagger-gradle-plugin") version "2.2.8"
    id("io.quarkus")
    id("jacoco")
}

group = "com.hoseus"
version = "0.1.0"

repositories {
    mavenLocal()
    mavenCentral()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

val hoseusLibLoggingVersion by extra("0.1.0")
val hoseusLibErrorVersion by extra("0.1.0")
val jnanoidVersion by extra("2.0.0")
val kotestVersion by extra("5.5.4")
val mockkVersion by extra("1.13.3")

dependencies {
    implementation(platform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))

    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-resteasy-reactive-jackson")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-resteasy-reactive")
    implementation("io.quarkus:quarkus-smallrye-health")
    implementation("io.quarkus:quarkus-opentelemetry-exporter-otlp")

    implementation("com.hoseus:lib-logging-quarkus:${hoseusLibLoggingVersion}")
    implementation("io.quarkus:quarkus-logging-gelf")

    implementation("com.hoseus:lib-error:${hoseusLibErrorVersion}")

    implementation("io.quarkus:quarkus-spring-cloud-config-client")
    implementation("io.quarkus:quarkus-config-yaml")

    implementation("io.quarkus:quarkus-jdbc-mysql")
    implementation("io.quarkus:quarkus-hibernate-orm-panache-kotlin")

    implementation("io.quarkus:quarkus-redis-client")

    implementation("io.quarkus:quarkus-hibernate-validator")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("com.aventrix.jnanoid:jnanoid:${jnanoidVersion}")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("io.kotest:kotest-runner-junit5:${kotestVersion}")
    testImplementation("io.kotest:kotest-assertions-core:${kotestVersion}")
    testImplementation("io.mockk:mockk:${mockkVersion}")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

noArg {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

allOpen {
    annotation("javax.ws.rs.Path")
    annotation("javax.enterprise.context.ApplicationScoped")
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

tasks.withType<io.swagger.v3.plugins.gradle.tasks.ResolveTask> {
    outputFileName = "openapi"
    outputFormat = io.swagger.v3.plugins.gradle.tasks.ResolveTask.Format.YAML
    prettyPrint = true
    classpath = sourceSets["main"].runtimeClasspath
    resourcePackages = setOf("com.hoseus")
    outputDir = file("apis-docs")
}

jacoco {
    toolVersion = "0.8.7"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    kotlinOptions.javaParameters = true
}

tasks.withType<Test> {
    useJUnitPlatform()
    extensions.configure(JacocoTaskExtension::class) {
        classDumpDir = layout.buildDirectory.dir("jacoco/classpathdumps").get().asFile
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.withType<JacocoReport> {
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true)
    }
}
