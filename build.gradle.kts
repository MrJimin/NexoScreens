import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.3.0"
    id("com.gradleup.shadow") version "9.2.2"
    id("java")
    `java-library`
}

group = "com.github.mrjimin.nexoscreens"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") // Paper
    maven("https://repo.nexomc.com/releases") // Nexo
    maven("https://repo.dmulloy2.net/repository/public/") // ProtocolLib
    maven("https://repo.extendedclip.com/releases/") // PlaceholderAPI
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:${rootProject.properties["paper_version"]}-R0.1-SNAPSHOT")
    compileOnly("com.nexomc:nexo:${rootProject.properties["nexo_version"]}") { exclude("*") }

<<<<<<< HEAD
    compileOnly("com.comphenix.protocol","ProtocolLib","5.3.0") // ProtocolLib
=======
    compileOnly("net.dmulloy2:ProtocolLib:5.4.0")
>>>>>>> 027534e (fixed an issue with command execution; updated the protocolLib dependency)
    compileOnly("me.clip","placeholderapi","2.11.6") // PlaceholderAPI

    implementation("org.bstats","bstats-bukkit","3.0.2") // bStats
}

kotlin {
    jvmToolchain(21)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

val shadowJarPlugin = tasks.register<ShadowJar>("shadowJarPlugin") {
    archiveFileName.set("NexoScreen-${project.version}.jar")

    destinationDirectory.set(file("${project.rootDir}/target"))

    from(sourceSets.main.get().output)
    configurations = listOf(project.configurations.runtimeClasspath.get())

}

tasks.named("build") {
    dependsOn(shadowJarPlugin)
}

tasks.compileJava {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}