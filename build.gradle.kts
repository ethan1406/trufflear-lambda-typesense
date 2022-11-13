import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("java")
}

group "com.trufflear"
version "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation ("org.jetbrains.kotlin:kotlin-stdlib")

    implementation ("com.google.code.gson:gson:2.10")

    implementation ("com.amazonaws:aws-lambda-java-core:1.2.1")
    implementation ("com.amazonaws:aws-lambda-java-events:3.11.0")
    runtimeOnly ("com.amazonaws:aws-lambda-java-log4j2:1.5.1")

    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    testImplementation("org.assertj:assertj-core:3.23.1")

    implementation(files("libs/typesense-java-0.0.3.jar"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict -Xstring-concat=indy-with-constants")
        jvmTarget = "11"
    }
}

tasks.withType<ShadowJar> {
    archiveFileName.set("app.jar")
}