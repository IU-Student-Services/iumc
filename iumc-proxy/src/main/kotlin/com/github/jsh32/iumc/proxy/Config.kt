package com.github.jsh32.iumc.proxy

import com.github.jsh32.iumc.proxy.server.ServerConfig
import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandSource
import net.kyori.adventure.text.minimessage.MiniMessage
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
class Config(
    @Comment("Server config")
    val server: ServerConfig = ServerConfig(),
    @Comment("List of messages")
    val messages: Messages = Messages(),
    @Comment("Text based commands")
    val commands: List<TextCommand> = listOf(
        TextCommand(
          "rules",
            """
                <bold><red>IU Minecraft Rules</red></bold>
                <gray>1. <white>Be respectful - No Bullying: </white>
                Everyone here is to enjoy and have fun. Harsh words and
                bullying aren't allowed. Remember, everyone behind the
                screen is a real person. Be kind and respectful!

                <gray>2. <white>No Griefing:</white>
                We value hard work and creativity, don't destroy other
                players' creations. Let's keep the game fun and enjoyable
                for everyone!

                <gray>3. <white>Have Fun:</white>
                This server is all about fun! Dive into this world, survive,
                build, explore! Just enjoy the game.

                <dark_gray>Breaking the rules may lead to penalties, including
                being banned from the server. This will not only ban your
                Minecraft account, but also your IU account.
            """.trimIndent()
        )
    )
)

@ConfigSerializable
class Messages(
    val onWelcomeMessage: String = """
        <gray>Hi <first_name>, welcome to <red>IU Minecraft</red>!
        This is a private SMP exclusively for IU students.
        We're happy to have you join our community!
        
        <gray>(Please be sure to read the <hover:show_text:'<red>Click to read the rules'><click:run_command:/rules><i><red>/rules</red></i></click></hover>)
    """.trimIndent(),

    val onJoinMessage: String = """
        <gray>Hi <first_name>, welcome back to <red>IU Minecraft</red>!
    """.trimIndent(),
)

@ConfigSerializable
class TextCommand(
    @Comment("Command name")
    val name: String = "",
    @Comment("Command text")
    val text: String = ""
) {
    /**
     * Converts this method into a BrigadierCommand.
     *
     * @return A BrigadierCommand object representing this method.
     */
    fun toCommand(): BrigadierCommand {
        val node = LiteralArgumentBuilder
            .literal<CommandSource>(name)
            .executes { ctx ->
                ctx.source.sendMessage(MiniMessage.miniMessage().deserialize(text))
                return@executes Command.SINGLE_SUCCESS
            }
            .build()

        return BrigadierCommand(node)
    }
}
