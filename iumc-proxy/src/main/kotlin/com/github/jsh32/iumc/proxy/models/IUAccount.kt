package com.github.jsh32.iumc.proxy.models

import com.github.jsh32.iumc.proxy.models.query.QIUAccount
import io.ebean.annotation.DbComment
import javax.persistence.*

/**
 * Represents an IU account.
 *
 * @param email The unique identifier for the account, typically the user's email address.
 * @param firstName The first name of the user.
 * @param lastName The last name of the user.
 * @param username The username of the user.
 */
@Entity(name = "iu_accounts")
@DbComment("Saved response from userinfo endpoint")
class IUAccount(
    @Column(unique = true)
    @DbComment("Subject, or the unique account identifier, should be users email")
    var email: String,
    var firstName: String,
    var lastName: String,
    var username: String,
) : BaseModel() {
    @OneToOne(mappedBy = "account")
    lateinit var player: Player

    companion object {
        val query = QIUAccount
    }
}
