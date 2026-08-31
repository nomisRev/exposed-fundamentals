import org.jetbrains.exposed.v1.plugin.core.migration.VersionFormat

buildscript {
    dependencies {
        classpath("org.flywaydb:flyway-gradle-plugin:13.3.0")
        classpath("org.flywaydb:flyway-database-postgresql:13.3.0")
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "2.4.20-RC2"
    id("org.flywaydb.flyway") version "13.3.0"
    id("org.jetbrains.exposed.plugin") version "1.5.0"
}

group = "org.jetbrains.exposed.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

flyway {
    url = "jdbc:postgresql://localhost:5432/example"
    user = "postgres"
    password = "password"
    baselineOnMigrate = true
    validateOnMigrate = true
}

exposed {
    migrations {
        tablesPackage = "org.jetbrains.exposed.example"
        testContainersImageName = "postgres:18.6-alpine"
        fileVersionFormat = VersionFormat.MAJOR_MINOR
    }
}

dependencies {
    implementation("org.jetbrains.exposed:exposed-core:1.5.0")
    implementation("org.jetbrains.exposed:exposed-dao:1.5.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.5.0")
    implementation("org.jetbrains.exposed:exposed-r2dbc:1.5.0")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:1.5.0")

    implementation("org.postgresql:postgresql:42.7.10")
    implementation("org.postgresql:r2dbc-postgresql:1.1.1.RELEASE")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}
