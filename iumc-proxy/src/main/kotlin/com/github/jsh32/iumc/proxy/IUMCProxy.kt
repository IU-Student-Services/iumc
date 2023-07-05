package com.github.jsh32.iumc.proxy

import com.github.jsh32.iumc.proxy.listeners.VerificationListener
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
import net.elytrium.limboapi.api.LimboFactory
import org.slf4j.Logger
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

        val application = IUMCApplication(config.config.server)

        server.eventManager.register(this, application)
        server.eventManager.register(this, VerificationListener(limboFactory, application))

        logger.info("IUMC initialized!")
    }
}
