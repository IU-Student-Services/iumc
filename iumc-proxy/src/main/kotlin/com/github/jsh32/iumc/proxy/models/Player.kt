package com.github.jsh32.iumc.proxy.models

import com.github.jsh32.iumc.proxy.models.query.QPlayer
import io.ebean.annotation.DbComment
import java.util.UUID
import javax.persistence.*

/**
 * Represents an in-game player.
 *
 * @property uuid The unique identifier of the player.
 * @property account The IUAccount associated with the player.
 */
@Entity(name = "players")
@DbComment("Map of in-game players to their IU account")
class Player(
    @Column(unique = true)
    @DbComment("Minecraft player UUID")
    val uuid: UUID,
    @Column(unique = true)
    @OneToOne
    @JoinColumn(name = "account")
    val account: IUAccount
) : BaseModel() {
    companion object {
        fun query() = QPlayer()
    }
}
