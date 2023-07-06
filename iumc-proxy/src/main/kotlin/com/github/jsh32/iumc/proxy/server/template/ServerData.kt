package com.github.jsh32.iumc.proxy.server.template

import com.velocitypowered.api.proxy.ProxyServer

class ServerData(
    server: ProxyServer,
    val publicIp: String
) {
    val onlinePlayers = server.playerCount
    val maxPlayers = server.configuration.showMaxPlayers
    // TODO: Make a maintenance mode command which can close the server.
    val open = true
}