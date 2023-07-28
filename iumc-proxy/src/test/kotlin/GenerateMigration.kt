@file:JvmName("GenerateMigration")

import io.ebean.annotation.Platform
import io.ebean.dbmigration.DbMigration
import java.nio.file.Path


fun main() {
    println("Migration generator")

    DbMigration.create().apply {
        addPlatform(Platform.H2)
        setPathToResources(
            Path.of(System.getProperty("user.dir") + "/src/main/resources").toAbsolutePath().toString()
        )
    }.generateMigration()
}