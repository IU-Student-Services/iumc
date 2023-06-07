package com.github.jsh32.iumc.proxy

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import org.slf4j.Logger
import java.nio.file.Path

@Plugin(
    id = "iumcproxy",
    name = "IUMC Proxy",
    description = "IUMC Proxy plugin",
    version = "0.1.0-SNAPSHOT",
    authors = ["JSH32"]
)
class IUMCProxy @Inject constructor(
    private val server: ProxyServer,
    private val logger: Logger,
    @DataDirectory dataDirectory: Path,
    private val suspendingPluginContainer: SuspendingPluginContainer
) {
    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        suspendingPluginContainer.initialize(this)
//        server.eventManager.registerSuspend(this, PlayerListener(orbitConnection))
        logger.info("AstroIsles proxy core initialized!")
    }
}
