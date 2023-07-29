package com.github.jsh32.iumc.proxy.listeners

import com.github.jsh32.iumc.proxy.Messages
import com.github.jsh32.iumc.proxy.models.query.QPlayer
import com.github.jsh32.iumc.proxy.server.IUMCApplication
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.proxy.Player
import net.elytrium.limboapi.api.Limbo
import net.elytrium.limboapi.api.LimboFactory
import net.elytrium.limboapi.api.LimboSessionHandler
import net.elytrium.limboapi.api.chunk.Dimension
import net.elytrium.limboapi.api.event.LoginLimboRegisterEvent
import net.elytrium.limboapi.api.material.Block
import net.elytrium.limboapi.api.player.GameMode
import net.elytrium.limboapi.api.player.LimboPlayer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder

/**
 * A class that listens for verification events and performs necessary actions.
 *
 * @param limboFactory The factory used to create instances of Limbo.
 */
class VerificationManager(
    limboFactory: LimboFactory,
    private val application: IUMCApplication,
    private val messages: Messages,
) {
    private val virtualWorld = run {
        val world = limboFactory.createVirtualWorld(Dimension.THE_END, 0.0, 100.0, 0.0, 0.0f, 0.0f)
        world.setBlock(0, 0, -1, limboFactory.createSimpleBlock(Block.BARRIER))
        world
    }

    private val limbo = limboFactory.createLimbo(virtualWorld).setName("IUVerify").setGameMode(GameMode.CREATIVE)

    /**
     * Starts the registration process for the given player.
     *
     * @param player the player for whom the registration process should be started
     */
    fun startVerification(player: Player) {
        limbo.spawnPlayer(player, LimboHandler(application, messages))
    }

    @Subscribe
    fun playerLoginListener(event: LoginLimboRegisterEvent) {
        val player = QPlayer().uuid.eq(event.player.uniqueId).findOne()

        if (player != null) {
            event.player.sendMessage(MiniMessage.miniMessage().deserialize(messages.onJoinMessage,
                Placeholder.component("first_name", Component.text(player.account.firstName))))
        } else {
            event.addOnJoinCallback { startVerification(event.player) }
        }
    }
}

/**
 * Responsible for handling Limbo sessions for a specific player.
 */
private class LimboHandler(
    private val application: IUMCApplication,
    private val messages: Messages,
) : LimboSessionHandler {
    lateinit var player: LimboPlayer

    override fun onSpawn(server: Limbo?, player: LimboPlayer?) {
        this.player = player!!

        val link = Component.text("here")
            .color(NamedTextColor.RED)
            .clickEvent(ClickEvent.openUrl(application.startRegistration(player.proxyPlayer, {
                player.disconnect()
                player.proxyPlayer.sendMessage(
                    MiniMessage.miniMessage().deserialize(messages.onWelcomeMessage,
                        Placeholder.component("first_name", Component.text(it.account.firstName))
                    ))
            },
            {
                player.proxyPlayer.disconnect(
                    Component.text("IU account")
                        .color(NamedTextColor.RED)
                        .append(Component.text(" is already linked! Try again with a different account")
                        .color(NamedTextColor.GRAY)))
            })))

        // Clear chat and give link
        player.proxyPlayer.sendMessage(
            Component.text("\n".repeat(100) + "Please verify your IU account ")
                .color(NamedTextColor.GRAY)
                .append(link))
    }

    override fun onDisconnect() {
        // Clear the chat.
        player.proxyPlayer.sendMessage(Component.text("\n".repeat(100)))
    }

    override fun onMove(posX: Double, posY: Double, posZ: Double) {
        player.teleport(0.0, 0.0, 0.0, 0.0f, 0.0f)
    }
}