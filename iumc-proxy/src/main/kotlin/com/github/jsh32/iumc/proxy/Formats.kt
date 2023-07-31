package com.github.jsh32.iumc.proxy

import com.github.jsh32.iumc.proxy.models.IUAccount
import com.github.jsh32.iumc.proxy.models.query.QPlayer
import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder

/**
 * Returns the formatted player information as a Component.
 */
fun Player.getPlayerFormat(): Component {
    val playerFormat = """
        <hover:show_text:'<gray>Username: <dark_gray><name></gray>
        <gray>Name: <dark_gray><iu_name></gray>
        <gray>UUID: <dark_gray><uuid></gray>
        <gray>Role: <dark_gray><role></gray>
        <gray>Server: <dark_gray><server_id></gray>'><gray><name> <dark_gray><italic>(<iu_name>)</italic></dark_gray></hover>
    """.trimIndent()

    val dbPlayer = QPlayer().uuid.eq(uniqueId).findOne()!!
    val nameColor = if (dbPlayer.admin) NamedTextColor.RED else NamedTextColor.GRAY
    val roleName = if (dbPlayer.admin) "Admin" else "Member"

    return MiniMessage.miniMessage().deserialize(
        playerFormat,
        Placeholder.component("name", Component.text(username).color(nameColor)),
        Placeholder.component("uuid", Component.text(uniqueId.toString())),
        Placeholder.component("server_id", Component.text(currentServer.get().serverInfo.name)),
        Placeholder.component("role", Component.text(roleName)),
        Placeholder.component("iu_name", Component.text("${dbPlayer.account.firstName} ${dbPlayer.account.lastName}"))
    )
}

fun IUAccount.getFormat(): Component {
    val format = """
        <gray>Firstname: <dark_gray><first_name></gray>
        <gray>Lastname: <dark_gray><last_name></gray>
        <gray>Username: <dark_gray><username></gray>
        <gray>Email: <dark_gray><email></gray>
    """.trimIndent()

    return MiniMessage.miniMessage().deserialize(
        format,
        Placeholder.component("first_name", Component.text(firstName)),
        Placeholder.component("last_name", Component.text(lastName)),
        Placeholder.component("username", Component.text(username)),
        Placeholder.component("email", Component.text(email))
    )
}
