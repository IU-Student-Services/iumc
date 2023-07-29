package com.github.jsh32.iumc.proxy.server

import com.github.jsh32.iumc.proxy.models.*
import com.github.jsh32.iumc.proxy.models.query.QIUAccount
import com.github.jsh32.iumc.proxy.server.responses.UserInfo
import com.github.jsh32.iumc.proxy.server.responses.respondFtl
import com.github.jsh32.iumc.proxy.server.template.ServerData
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.proxy.ProxyServer
import freemarker.cache.ClassTemplateLoader
import io.ebean.annotation.Transactional
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.routing.*
import java.net.URL
import com.velocitypowered.api.proxy.Player as ProxyPlayer

/**
 * Represents the types of verifications that can be performed.
 */
private enum class VerificationType {
    LINK,
    REFRESH
}

/**
 * Represents various types of verification errors that can occur during the verification process.
 */
private sealed class VerificationError {
    data class AlreadyLinked(val username: String) : VerificationError()
    data class IncorrectAccount(val iuUserInfo: UserInfo) : VerificationError()
}

/**
 * Represents the state of a verification process.
 *
 * @property type The type of verification being performed.
 * @property player The player associated with the verification.
 * @property onCompleted Callback function to be called when the verification is completed.
 *                      It receives the successfully verified player as a parameter.
 * @property onError Callback function to be called when an error occurs during verification.
 *                   It receives the error object as a parameter.
 */
private data class State(
    val type: VerificationType,
    val player: ProxyPlayer,
    val onCompleted: (player: Player) -> Unit,
    val onError: (error: VerificationError) -> Unit,
)

class IUMCApplication(
    private val server: ProxyServer,
    private val config: ServerConfig
) {
    private val sessionManager = OAuthSessionManager<State>()

    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    /**
     * Starts the refresh process for a given player.
     *
     * @param player The proxy player to start the refresh process for.
     * @param onCompleted Callback function to be executed when the refresh process is completed.
     *                    Receives the refreshed player as a parameter.
     * @param onIncorrectAccount Callback function to be executed when the refresh process fails due to
     *                           an incorrect account. Receives the user information for the incorrect account
     *                           as a parameter.
     * @return The created session ID for the refresh process.
     */
    fun startRefresh(
        player: ProxyPlayer,
        onCompleted: (player: Player) -> Unit,
        onIncorrectAccount: (iuUserInfo: UserInfo) -> Unit,
    ): String {
        return sessionManager.createPreStateSession(State(
            VerificationType.REFRESH,
            player,
            onCompleted
        ) {
            when (it) {
                is VerificationError.IncorrectAccount -> onIncorrectAccount(it.iuUserInfo)
                else -> {}
            }
        }, config.address)
    }

    /**
     * Starts the registration process for a player.
     *
     * @param player The player object to register. Must be a ProxyPlayer.
     * @param onCompleted Callback function called when registration is completed. Receives the registered Player object as a parameter.
     * @param onAlreadyLinked Callback function called when the player is already linked to an account. Receives the player's username as a parameter.
     *
     * @return A string representing the session ID for the registration process.
     */
    fun startRegistration(
        player: ProxyPlayer,
        onCompleted: (player: Player) -> Unit,
        onAlreadyLinked: (username: String) -> Unit,
    ): String {
        return sessionManager.createPreStateSession(State(
            VerificationType.LINK,
            player,
            onCompleted
        ) {
            when (it) {
                is VerificationError.AlreadyLinked -> onAlreadyLinked(it.username)
                else -> {}
            }
        }, config.address)
    }

    init {
        embeddedServer(Netty, port = config.port, host = config.host, module = { module(this) })
            .start(wait = false)
    }

    @Subscribe
    private fun onPlayerDisconnect(event: DisconnectEvent) {
        sessionManager.deleteByFilter { it.player == event.player }
    }

    /**
     * Configure application.
     */
    private fun module(app: Application) {
        app.install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) { json() }
        app.install(StatusPages) {
            exception<Throwable> { call, cause ->
                call.respondFtl(
                    "error.ftl", mapOf(
                        "message" to cause.message!!,
                    )
                )
            }

            status(HttpStatusCode.NotFound) { call, _ ->
                call.respondFtl(
                    "error.ftl", mapOf(
                        "message" to "404: Page Not Found",
                    )
                )
            }
        }

        app.install(Authentication) {
            oauth("iu-login") {
                @Suppress("UNCHECKED_CAST")
                providerLookup = { config.oauthConfig.toOAuthServerSettings(sessionManager as OAuthSessionManager<Any>) }
                urlProvider = { config.oauthConfig.callback }
                client = HttpClient()
            }
        }

        app.install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "frontend")
        }

        app.routing {
            staticResources("/static", "frontend/static")

            get("/") {
                call.respondFtl("index.ftl", mapOf("server" to ServerData(server, config.publicIp)))
            }

            authenticate("iu-login") {
                get("/auth/login") {}

                get(URL(config.oauthConfig.callback).path) {
                    val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
                    val state = principal?.state?.let { it1 -> sessionManager.getStateAndRemove(it1) }!!

                    val response = client.request(config.oauthConfig.userInfo) {
                        bearerAuth(principal.accessToken)
                    }

                    val accountInfo = response.body<UserInfo>()

                    // IU account with linked email address.
                    val foundAccount = QIUAccount().email.eq(accountInfo.sub).findOne()

                    if (state.type == VerificationType.REFRESH) {
                        // Successfully re-authenticated with the correct account
                        if (foundAccount != null && foundAccount.player.uuid == state.player.uniqueId) {
                            // Update all details with new ones.
                            foundAccount.firstName = accountInfo.givenName
                            foundAccount.lastName = accountInfo.sn
                            foundAccount.username = accountInfo.username
                            foundAccount.email = accountInfo.sub
                            foundAccount.save()

                            call.respondFtl(
                                "re_authenticated.ftl", mapOf(
                                    "account" to foundAccount,
                                    "username" to foundAccount.player.username,
                                )
                            )

                            state.onCompleted(foundAccount.player)
                        } else {
                            call.respondFtl(
                                "incorrect_account.ftl", mapOf(
                                    "account" to accountInfo,
                                    "username" to state.player.username,
                                )
                            )

                            state.onError(VerificationError.IncorrectAccount(accountInfo))
                        }
                    } else {
                        if (foundAccount != null) {
                            call.respondFtl(
                                "already_linked.ftl", mapOf(
                                    "account" to foundAccount,
                                    "username" to foundAccount.player.username,
                                )
                            )

                            state.onError(VerificationError.AlreadyLinked(foundAccount.player.username))
                        } else {
                            val player = createPlayer(state.player, accountInfo)

                            call.respondFtl(
                                "linked.ftl", mapOf(
                                    "account" to player.account,
                                    "player" to state.player
                                )
                            )

                            state.onCompleted(player)
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates a new player record and saves it to the database.
     *
     * @param player The player object representing the player in the game.
     * @param userInfo The user information containing details about the player.
     * @return The created player record.
     */
    @Transactional
    private fun createPlayer(player: ProxyPlayer, userInfo: UserInfo): Player {
        val account = IUAccount(
            userInfo.sub,
            userInfo.givenName,
            userInfo.sn,
            userInfo.username
        )

        account.save()

        val playerRecord = Player(
            player.uniqueId,
            player.username,
            account
        )

        playerRecord.save()

        return playerRecord
    }
}
