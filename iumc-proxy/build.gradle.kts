plugins {
    id("kotlin-project")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
}

repositories {
    maven(uri("https://repo.papermc.io/repository/maven-public/"))
    maven(uri("https://maven.elytrium.net/repo/"))
}

dependencies {
    implementation("com.github.shynixn.mccoroutine:mccoroutine-velocity-api:2.12.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-velocity-core:2.12.0")

    implementation("net.kyori:adventure-text-minimessage:4.13.1")
    implementation("io.ktor:ktor-client-cio-jvm:2.3.1")

    compileOnly("com.velocitypowered:velocity-api:3.0.1")
    kapt("com.velocitypowered:velocity-api:3.0.1")

    implementation("org.spongepowered:configurate-hocon:4.1.2")

    compileOnly("net.elytrium:limboapi-api:1.0.7")

    implementation("ch.qos.logback:logback-classic:1.4.8")

    // Ktor
    implementation("io.ktor:ktor-server-content-negotiation:2.3.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.1")
    implementation("io.ktor:ktor-server-core:2.3.1")
    implementation("io.ktor:ktor-server-plugins:2.3.1")
    implementation("io.ktor:ktor-server-auth:2.3.1")
    implementation("io.ktor:ktor-server-netty:2.3.1")
    implementation("io.ktor:ktor-server-status-pages:2.3.1")
    implementation("io.ktor:ktor-server-call-logging:2.3.1")
}
