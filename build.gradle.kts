plugins {
    id("org.jetbrains.kotlin.jvm") version "2.4.20-RC2"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.4.20-RC2"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.exposed:exposed-core:1.5.0")
    implementation("org.jetbrains.exposed:exposed-dao:1.5.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.5.0")
    implementation("org.jetbrains.exposed:exposed-r2dbc:1.5.0")
    implementation("org.jetbrains.exposed:exposed-json:1.5.0")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.19.2")
    implementation("com.zaxxer:HikariCP:7.0.2")
    implementation("org.flywaydb:flyway-core:13.3.0")
    implementation("org.postgresql:postgresql:42.7.10")
    implementation("org.postgresql:r2dbc-postgresql:1.1.1.RELEASE")
    // Only `@Transactional` on one slide; the deck does not run Spring.
    compileOnly("org.springframework:spring-tx:6.2.11")
}

kotlin {
    jvmToolchain(21)
    compilerOptions.freeCompilerArgs.addAll("-Xcollection-literals")
}

val snippetsCheck by tasks.registering(Exec::class) {
    commandLine("npx", "slidev-kotlin-snippets", "--check")
}

tasks.check { dependsOn(snippetsCheck) }
