
rootProject.name = "IU-Minecraft"

dependencyResolutionManagement {
    includeBuild("build-logic")
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

include (
    "iumc-core"
)
include("iumc-proxy")
