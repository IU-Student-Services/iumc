package com.github.jsh32.iumc.proxy

import com.github.jsh32.iumc.proxy.server.ServerConfig
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
class Config {
    @Comment("Server config")
    val server = ServerConfig()
}