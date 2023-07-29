package com.github.jsh32.iumc.proxy

import com.github.jsh32.iumc.proxy.listeners.VerificationManager
import com.github.jsh32.iumc.proxy.models.query.QPlayer
import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

/**
 * Unlinks a player's account and starts the verification process.
 *
 * @param manager The VerificationManager instance used to start the verification process.
 * @return A BrigadierCommand instance representing the "unlink" command.
 */
fun unlinkCommand(manager: VerificationManager): BrigadierCommand {
    val node = LiteralArgumentBuilder
        .literal<CommandSource>("unlink")
        .executes { ctx ->
            val player = ctx.source as Player

            val link = QPlayer().uuid.eq(player.uniqueId)
            link.delete()

            manager.startVerification(player)

            return@executes Command.SINGLE_SUCCESS
        }
        .build()

    return BrigadierCommand(node)
}