plugins {
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"
    id("org.jlleitschuh.gradle.ktlint") version "11.3.1"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(group = "org.postgresql", name = "postgresql", version = "42.+")
    implementation(group = "org.http4k", name = "http4k-core", version = "4.40.+")
    implementation(group = "org.http4k", name = "http4k-server-jetty", version = "4.40.+")
    implementation(group = "org.http4k", name = "http4k-client-okhttp", version = "4.40.+")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = "1.5.+")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-datetime", version = "0.4.+")
    implementation(group = "org.slf4j", name = "slf4j-api", version = "2.0.0-alpha5")
    implementation("org.junit.jupiter:junit-jupiter:5.7.0")
    implementation(platform("org.http4k:http4k-bom:4.41.1.1"))
    implementation("org.http4k:http4k-testing-hamkrest")
    testImplementation("org.testng:testng:7.1.0")
    runtimeOnly(group = "org.slf4j", name = "slf4j-simple", version = "2.0.0-alpha5")
    testImplementation(kotlin("test"))
}

tasks.register<Copy>("copyRuntimeDependencies") {
    into("build/libs")
    from(configurations.runtimeClasspath)
}

tasks.register<JavaExec>("launch") {
    group = "launch"
    this.mainClass.set("pt.isel.ls.boardio.AppLauncherKt")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.named<Jar>("jar") {
    dependsOn("copyRuntimeDependencies")
    manifest {
        attributes["Main-Class"] = "pt.isel.ls.boardio.AppLauncherKt"
        attributes["Class-Path"] = configurations.runtimeClasspath.get().joinToString(" ") { it.name }
    }
}
