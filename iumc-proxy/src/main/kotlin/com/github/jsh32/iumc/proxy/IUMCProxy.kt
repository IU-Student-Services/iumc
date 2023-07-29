package com.github.jsh32.iumc.proxy

import com.github.jsh32.iumc.proxy.listeners.VerificationManager
import com.github.jsh32.iumc.proxy.models.IUAccount
import com.github.jsh32.iumc.proxy.models.Player
import com.github.jsh32.iumc.proxy.server.IUMCApplication
import com.github.jsh32.iumc.proxy.utils.loadConfig
import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import io.ebean.Database
import io.ebean.DatabaseFactory
import io.ebean.config.DatabaseConfig
import io.ebean.datasource.DataSourceConfig
import io.ebean.migration.MigrationConfig
import io.ebean.migration.MigrationRunner
import net.elytrium.limboapi.api.LimboFactory
import org.slf4j.Logger
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Plugin(
    id = "iumcproxy",
    name = "IUMC Proxy",
    description = "IUMC Proxy plugin",
    version = "0.1.0-SNAPSHOT",
    authors = ["JSH32"],
    dependencies = [Dependency(id = "limboapi")]
)
class IUMCProxy @Inject constructor(
    private val server: ProxyServer,
    private val logger: Logger,
    @DataDirectory private val dataDirectory: Path,
    private val suspendingPluginContainer: SuspendingPluginContainer
) {
    private val limboFactory = server.pluginManager.getPlugin("limboapi").flatMap(PluginContainer::getInstance).orElseThrow() as LimboFactory

    @Subscribe
    private fun onProxyInitialization(event: ProxyInitializeEvent) {
        suspendingPluginContainer.initialize(this)

        val config = loadConfig<Config>(
            Paths.get(dataDirectory.toString(), "config.conf").toFile(), true)

        // Initialize h2 db
        initDatabase()

        val application = IUMCApplication(server, config.config.server)

        val verificationManager = VerificationManager(limboFactory, application, config.config.messages)

        server.eventManager.register(this, application)
        server.eventManager.register(this, verificationManager)

        // Register commands
        val commandManager = server.commandManager

        // All text based commands
        for (command in config.config.commands) {
            commandManager.register(command.toCommand())
        }

        commandManager.register(unlinkCommand(verificationManager))

        logger.info("IUMC initialized!")
    }

    private fun initDatabase(): Database {
        val url = "jdbc:h2:${File(dataDirectory.toFile(), "database").absolutePath}"

        val dataSourceConfig = DataSourceConfig()
        dataSourceConfig.setUrl(url)
        dataSourceConfig.setUsername("")
        dataSourceConfig.setPassword("")

        val dbConfig = DatabaseConfig()
        dbConfig.dataSourceConfig = dataSourceConfig
        dbConfig.isDefaultServer = true
        dbConfig.classes = listOf(
            IUAccount::class.java,
            Player::class.java
        )

        val previousClassLoader = Thread.currentThread().getContextClassLoader()
        Thread.currentThread().setContextClassLoader(javaClass.classLoader)

        // Initialize the database
        val database = DatabaseFactory.create(dbConfig)

        // Run available migrations
        val migrationConfig = MigrationConfig()
        migrationConfig.migrationPath = "classpath:/dbmigration/h2"
        migrationConfig.load(Properties())
        val runner = MigrationRunner(migrationConfig)
        runner.run(database.dataSource())

        // Set the original class loader back
        Thread.currentThread().setContextClassLoader(previousClassLoader)

        return database
    }
}
