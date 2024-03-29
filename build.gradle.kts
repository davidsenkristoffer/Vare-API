val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val exposedVersion = "0.36.1"
val h2Version = "2.0.202"
val hikariCpVersion = "5.0.0"
val assertjVersion = "3.16.1"
val restAssuredVersion = "4.3.1"
val junitVersion = "5.6.2"

plugins {
    application
    kotlin("jvm") version "1.5.30"
    kotlin("plugin.serialization") version "1.5.30"
    id("org.flywaydb.flyway") version "5.2.4"
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

group = "h577870"
version = "0.0.1"

repositories {
    mavenLocal()
    jcenter()
    maven(url = "https://kotlin.bintray.com/kotlinx/")
    mavenCentral()
}
/*
Trenger jeg alle disse? Det finner vi ut av en annen dag...
 */
dependencies {
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-jackson:$ktor_version")
    implementation("io.ktor:ktor-websockets:$ktor_version")
    implementation("io.ktor:ktor-auth-jwt:$ktor_version")
    implementation("com.zaxxer:HikariCP:$hikariCpVersion")
    implementation("com.h2database:h2:$h2Version")
    implementation("org.flywaydb:flyway-core:8.1.0")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.springframework.security:spring-security-crypto:5.5.1")
    implementation("org.postgresql:postgresql:42.3.1")
    implementation("commons-logging:commons-logging:1.2")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.1")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
}
flyway {
    url = System.getenv("DB_URL")
    user = System.getenv("DB_USER")
    password = System.getenv("DB_PASSWORD")
    baselineOnMigrate=true
    locations = arrayOf("filesystem:resources/db/migrations")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = application.mainClassName
    }
}