plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.8.10"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
    implementation("org.jetbrains.kotlin:kotlin-serialization:1.8.10")
    implementation("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")
    implementation("net.minecrell.plugin-yml.bukkit:net.minecrell.plugin-yml.bukkit.gradle.plugin:0.5.3")
    implementation("org.jetbrains.kotlin.kapt:org.jetbrains.kotlin.kapt.gradle.plugin:1.8.21")
}