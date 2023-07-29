package com.github.jsh32.iumc.proxy

import com.github.jsh32.iumc.proxy.listeners.VerificationManager
import com.github.jsh32.iumc.proxy.models.query.QPlayer
import com.github.jsh32.iumc.proxy.server.IUMCApplication
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration

/**
 * Checks if the CommandSource has administrative privileges.
 *
 * @return true if the CommandSource has administrative privileges, false otherwise.
 */
fun CommandSource.isAdmin(): Boolean {
    return if (this is Player) {
        QPlayer().uuid.eq(uniqueId).findOne()!!.admin
    } else {
        // Sender is console
        true
    }
}

/**
 * Unlinks a player's account and starts the verification process.
 *
 * @param manager The VerificationManager instance used to start the verification process.
 * @return command
 */
fun unlinkCommand(manager: VerificationManager): BrigadierCommand {
    val node = LiteralArgumentBuilder
        .literal<CommandSource>("unlink")
        .executes { ctx ->
            val player = ctx.source as Player

            val link = QPlayer().uuid.eq(player.uniqueId)
            link.delete()

            manager.startVerification(player)

            Command.SINGLE_SUCCESS
        }
        .build()

    return BrigadierCommand(node)
}

/**
 * Sets the admin status of a player.
 *
 * @return command
 */
fun setAdminCommand(): BrigadierCommand {
    val node = LiteralArgumentBuilder
        .literal<CommandSource>("setAdmin")
        .requires { it.isAdmin() }
        .then(RequiredArgumentBuilder.argument<CommandSource, String>("player", StringArgumentType.word())
            .suggests { _, builder ->
                // Optimize to only search for the content which has been entered.
                QPlayer().findEach { builder.suggest(it.username) }
                builder.buildFuture()
            }
            .then(RequiredArgumentBuilder.argument<CommandSource, Boolean>("isAdmin", BoolArgumentType.bool())
            .executes {
                val arg = it.getArgument("player", String::class.java)
                val admin = it.getArgument("isAdmin", Boolean::class.java)

                val player = QPlayer().username.eq(arg).findOne()
                if (player != null) {

                    val text = when (admin) {
                        true -> when (player.admin) {
                            true -> "was already an admin."
                            false -> "is now an admin."
                        }
                        false -> when (player.admin) {
                            true -> "is no longer an admin."
                            false -> "was not an admin."
                        }
                    }

                    player.admin = admin
                    player.save()

                    it.source.sendMessage(Component.text("${player.username} $text").color(NamedTextColor.GRAY))
                } else {
                    it.source.sendMessage(Component.text("A user named '$arg' was not found."))
                }

                Command.SINGLE_SUCCESS
            }))
        .build()

    return BrigadierCommand(node)
}

/**
 * Refreshes the IU account data for the current player.
 *
 * @param application the application to use for authentication.
 * @return command
 */
fun refreshCommand(application: IUMCApplication): BrigadierCommand {
    val node = LiteralArgumentBuilder
        .literal<CommandSource>("refresh")
        .executes { ctx ->
            val player = ctx.source as Player

            val url = application.startRefresh(player, {
                player.sendMessage(
                    Component.text("Account refreshed")
                        .style(Style.style(NamedTextColor.RED, TextDecoration.BOLD)))

                player.sendMessage(it.account.getFormat())
            }, {
                player.sendMessage(Component.text("Logged in with incorrect IU Account").color(NamedTextColor.RED))
            })

            val link = Component.text("here")
                .color(NamedTextColor.RED)
                .clickEvent(ClickEvent.openUrl(url))

            player.sendMessage(
                Component.text("Account refresh started, please click ")
                    .color(NamedTextColor.GRAY)
                    .append(link))

            Command.SINGLE_SUCCESS
        }
        .build()

    return BrigadierCommand(node)
}