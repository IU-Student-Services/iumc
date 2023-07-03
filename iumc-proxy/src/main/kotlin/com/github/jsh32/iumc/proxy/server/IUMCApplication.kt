package com.github.jsh32.iumc.proxy.server

import com.github.jsh32.iumc.proxy.server.responses.messageRespond
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

class IUMCApplication(private val config: ServerConfig) {
    private val sessionManager = OAuthSessionManager<UUID>()

    fun getVerificationUrl(playerId: UUID) = sessionManager.createPreStateSession(playerId, config.address)

    init {
        embeddedServer(Netty, port = config.port, host = config.host, module = { module(this) })
            .start(wait = false)
    }

    @Subscribe
    fun onPlayerDisconnect(event: DisconnectEvent) {
        sessionManager.deleteByValue(event.player.uniqueId)
    }

    /**
     * Configure application.
     */
    private fun module(app: Application) {
        app.install(ContentNegotiation) { json() }
        app.install(StatusPages) {
            // This is needed because of onStateCreated
            exception<BadRequestException> { call, cause ->
                call.messageRespond(HttpStatusCode.BadRequest, cause.message!!)
            }
            exception<Throwable> { call, cause ->
                println(cause.message)
                call.respondText { cause.message!! }
            }
        }

        app.install(Authentication) {
            oauth("iu-login") {
                providerLookup = { config.oauthConfig.toOAuthServerSettings(sessionManager) }
                urlProvider = { config.oauthConfig.callback }
                client = HttpClient()
            }
        }

        app.routing {
            authenticate("iu-login") {
                get("/auth/login") {}

//                get("/auth/oidc/redirect") {
//                    call.respond("Player UUID: bruh")
//                }

                get("/auth/oidc/redirect") {
                    println("Something")
//                    val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
//                    val player = principal?.state?.let { it1 -> sessionManager.getStateAndRemove(it1) }
                    call.respond("Player UUID: ")
//                    call.sessions.set(UserSession(principal!!.state!!, principal.accessToken))
//                    val redirect = redirects[principal.state!!]
//                    call.respondRedirect(redirect!!)
                }
            }
        }
    }
}
