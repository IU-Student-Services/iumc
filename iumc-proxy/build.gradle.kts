plugins {
    id("kotlin-project")
}

repositories {
    maven(uri("https://repo.papermc.io/repository/maven-public/"))
}

dependencies {
    implementation("com.github.shynixn.mccoroutine:mccoroutine-velocity-api:2.12.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-velocity-core:2.12.0")

    implementation("net.kyori:adventure-text-minimessage:4.13.1")

    compileOnly("com.velocitypowered:velocity-api:3.0.1")
    kapt("com.velocitypowered:velocity-api:3.0.1")
}