@file:Suppress("SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.utils.addToStdlib.ifTrue
import java.util.Date

plugins {
    kotlin("jvm") version "1.7.20"
    kotlin("plugin.serialization") version "1.7.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("java")
    `maven-publish`
    application
}

group = "de.christianbernstein.rsab"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktorVersion: String by project
val logbackVersion: String by project

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-client-core:$ktorVersion")
    testImplementation("io.ktor:ktor-client-cio:$ktorVersion")
    testImplementation("io.ktor:ktor-client-websockets:$ktorVersion")

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}

/**
 * Update the version / build-result file
 */
tasks.create("incrementVersion") {
    group = "iris"
    doLast {
        val gson = org.jetbrains.kotlin.com.google.gson.GsonBuilder().setPrettyPrinting().serializeNulls().create()
        val fileName = "iris.version.json"
        val chronosFile = File(projectDir, fileName)
        val creationTimestamp = Date().toInstant().epochSecond
        var buildNumber = 0
        // Read version file
        chronosFile.exists().ifTrue {
            val content = chronosFile.readText()
            val kvs = gson.fromJson(content, HashMap::class.java)
            buildNumber = (kvs["buildNumber"] as Double? ?: 0).toInt()
        }
        // Write version file
        chronosFile.writeText(gson.toJson(mapOf<String, Any>(
            "buildNumber" to buildNumber + 1,
            "creationTimestamp" to creationTimestamp
        )))
    }
}

/**
 * Execute "jar"-task after "incrementVersion"-task
 * TODO: Add 'iris.version.json' file to jar
 */
tasks.getByName("jar") {
    dependsOn(tasks.getByName("incrementVersion"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
        }
    }
}
