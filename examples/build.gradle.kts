import org.jetbrains.exposed.v1.plugin.core.migration.VersionFormat

buildscript {
    dependencies {
        classpath("org.flywaydb:flyway-gradle-plugin:13.3.0")
        classpath("org.flywaydb:flyway-database-postgresql:13.3.0")
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "2.4.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.4.10"
    id("org.flywaydb.flyway") version "13.3.0"
    id("org.jetbrains.exposed.plugin") version "1.4.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

flyway {
    url = "jdbc:postgresql://localhost:5432/postgres"
    user = "postgres"
    password = ""
    baselineOnMigrate = true
    validateOnMigrate = true
}

exposed {
    migrations {
        tablesPackage = "org.example"
        testContainersImageName = "postgres:18.6-alpine"
        fileVersionFormat = VersionFormat.MAJOR_MINOR
    }
}

dependencies {
    implementation("org.jetbrains.exposed:exposed-core:1.4.0")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:1.4.0")
    implementation("org.jetbrains.exposed:exposed-money:1.4.0")
    implementation("org.javamoney.moneta:moneta-core:1.4.5")
    implementation("org.jetbrains.exposed:exposed-dao:1.4.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.4.0")
    implementation("org.jetbrains.exposed:exposed-r2dbc:1.4.0")
    implementation("org.jetbrains.exposed:exposed-json:1.4.0")
    implementation("org.jetbrains.exposed:exposed-crypt:1.4.0")
    implementation("org.jetbrains.exposed:exposed-java-time:1.4.0")
    implementation("org.jetbrains.exposed:exposed-jodatime:1.4.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xcollection-literals")
    }
}

tasks.test {
    useJUnitPlatform()
}
