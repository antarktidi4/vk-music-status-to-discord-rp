import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    application
}

group = "com.hash"
version = ""

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.hash.Main")
}

dependencies {
    implementation("org.json:json:20211205")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<Jar>() {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveFileName.set("test.jar")
    manifest {
        attributes["Main-Class"] = application.mainClass
    }

    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
}