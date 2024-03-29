import java.io.ByteArrayOutputStream

plugins {
    id("kotlin-project")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
    id("io.ebean") version "13.6.5"
}

val generateGitProperties = tasks.register("generateGitProperties") {
    val gitPropertiesFile = file("$buildDir/generated/resources/git.properties")
    outputs.file(gitPropertiesFile)

    doLast {
        val gitHashOs = ByteArrayOutputStream()
        exec {
            commandLine = listOf("git", "rev-parse", "--short=7", "HEAD")
            standardOutput = gitHashOs
        }
        val gitHash = gitHashOs.toString().trim()

        val changes = ByteArrayOutputStream()
        exec {
            commandLine = listOf("git", "status", "-s")
            standardOutput = changes
        }
        val isModified = changes.toString().trim().isNotEmpty()

        gitPropertiesFile.writeText("git.hash=${if (isModified) "$gitHash-modified" else gitHash}")
    }
}

tasks.named("classes") {
    dependsOn(generateGitProperties)
}

sourceSets {
    main {
        resources {
            srcDir("$buildDir/generated/resources") // will include git.properties
        }
    }
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
    implementation("io.ktor:ktor-server-core-jvm:2.3.1")
    implementation("io.ktor:ktor-server-freemarker-jvm:2.3.1")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.1")

    compileOnly("com.velocitypowered:velocity-api:3.0.1")
    kapt("com.velocitypowered:velocity-api:3.0.1")

    implementation("org.spongepowered:configurate-hocon:4.1.2")

    compileOnly("net.elytrium:limboapi-api:1.0.7")

    implementation("ch.qos.logback:logback-classic:1.4.8")

    implementation("org.reflections:reflections:0.10.2")

    // Ktor
    implementation("io.ktor:ktor-server-content-negotiation:2.3.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.1")
    implementation("io.ktor:ktor-server-core:2.3.1")
    implementation("io.ktor:ktor-server-plugins:2.3.1")
    implementation("io.ktor:ktor-server-auth:2.3.1")
    implementation("io.ktor:ktor-server-netty:2.3.1")
    implementation("io.ktor:ktor-server-status-pages:2.3.1")
    implementation("io.ktor:ktor-server-call-logging:2.3.1")
    implementation("io.ktor:ktor-server-freemarker:2.3.1")

    // Database
    implementation("com.h2database:h2:2.2.220")

    val ebeanVersion = "13.6.5"
    implementation("io.ebean:ebean:$ebeanVersion")
    implementation("io.ebean:ebean-querybean:$ebeanVersion")
    implementation("io.ebean:ebean-migration:13.6.0")
    implementation("io.ebean:ebean-ddl-generator:$ebeanVersion")
    kapt("io.ebean:kotlin-querybean-generator:$ebeanVersion")

    // Needed for db migration
    testImplementation("javax.xml.bind:jaxb-api:2.3.1")
    testImplementation("com.sun.xml.bind:jaxb-core:2.3.0.1")
    testImplementation("com.sun.xml.bind:jaxb-impl:2.3.0.1")
    testImplementation("org.glassfish.jaxb:jaxb-runtime:2.3.1")
}

tasks.register("createMigration", JavaExec::class) {
    group = "Execution"
    description = "Create a migration using current model data"
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("GenerateMigration")
}