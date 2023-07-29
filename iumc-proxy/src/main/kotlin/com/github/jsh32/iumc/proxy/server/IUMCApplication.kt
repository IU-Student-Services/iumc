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

class IUMCApplication(
    private val server: ProxyServer,
    private val config: ServerConfig
) {
    private data class State(
        val player: com.velocitypowered.api.proxy.Player,
        val onCompleted: (player: Player) -> Unit,
        val onAlreadyLinked: (username: String) -> Unit
    )

    private val sessionManager = OAuthSessionManager<State>()

    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    fun startRegistration(
        player: com.velocitypowered.api.proxy.Player,
        onCompleted: (player: Player) -> Unit,
        onAlreadyLinked: (username: String) -> Unit
    ): String {
        return sessionManager.createPreStateSession(State(player, onCompleted, onAlreadyLinked), config.address)
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
                    val state = principal?.state?.let { it1 -> sessionManager.getStateAndRemove(it1) }

                    val response = client.request(config.oauthConfig.userInfo) {
                        bearerAuth(principal!!.accessToken)
                    }

                    val accountInfo = response.body<UserInfo>()

                    val foundAccount = QIUAccount().email.eq(accountInfo.sub).findOne()
                    if (foundAccount != null) {
                        call.respondFtl(
                            "already_linked.ftl", mapOf(
                                "account" to foundAccount,
                                "username" to foundAccount.player.username,
                            )
                        )

                        state!!.onAlreadyLinked(foundAccount.player.username)
                    } else {
                        val player = createPlayer(state!!.player, accountInfo)

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

    /**
     * Creates a player with the given UUID and user information.
     *
     * @param uuid the UUID of the player
     * @param userInfo the IU account info associated
     * @return the persisted player object
     */
    @Transactional
    private fun createPlayer(player: com.velocitypowered.api.proxy.Player, userInfo: UserInfo): Player {
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
