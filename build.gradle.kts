import kotlinx.knit.KnitTask

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlinx:kotlinx-knit:0.5.1")
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "2.4.10"
}

apply(plugin = "kotlinx-knit")

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.exposed:exposed-core:1.4.0")
    implementation("org.jetbrains.exposed:exposed-dao:1.4.0")
    implementation("org.jetbrains.exposed:exposed-json:1.4.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.4.0")
    implementation("org.jetbrains.exposed:exposed-crypt:1.4.0")
    implementation("org.jetbrains.exposed:exposed-money:1.4.0")
    implementation("tools.jackson.module:jackson-module-kotlin:3.2.+")
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<KnitTask>().configureEach {
    rootDir = project.projectDir
    files = project.fileTree(project.rootDir) {
        include("**/*.md")
        include("**/*.kt")
        include("**/*.kts")
        exclude("**/build/*")
        exclude("**/.gradle/*")
        exclude("**/node_modules/*")
    }
}
