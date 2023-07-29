package com.github.jsh32.iumc.proxy.listeners

import com.github.jsh32.iumc.proxy.getPlayerFormat
import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.PlayerChatEvent
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder

class ChatListener(private val server: ProxyServer) {
    @Subscribe(order = PostOrder.LAST)
    private fun chatEvent(event: PlayerChatEvent) {
        if (event.message.startsWith("/") || !event.result.isAllowed) {
            return
        }

        val message = MiniMessage.miniMessage().deserialize(
            "<player> <gray>» <reset><message>",
            Placeholder.component("player", event.player.getPlayerFormat()),
            Placeholder.component("message", Component.text(event.message))
        )

        for (player in server.allPlayers) {
            player.sendMessage(message)
        }

        // Don't send message to the server
        event.result = PlayerChatEvent.ChatResult.denied()
    }
}
