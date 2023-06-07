plugins {
    id("kotlin-project")
    id("net.minecrell.plugin-yml.bukkit")
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
}
