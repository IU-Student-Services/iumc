package com.github.jsh32.iumc.proxy.server

import com.github.jsh32.iumc.proxy.server.responses.respondFtl
import com.github.jsh32.iumc.proxy.server.template.ServerData
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.proxy.ProxyServer
import freemarker.cache.ClassTemplateLoader
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.net.URL
import java.util.*

class IUMCApplication(
    private val server: ProxyServer,
    private val config: ServerConfig
) {
    private val sessionManager = OAuthSessionManager<UUID>()

    fun getVerificationUrl(playerId: UUID) = sessionManager.createPreStateSession(playerId, config.address)

    init {
        embeddedServer(Netty, port = config.port, host = config.host, module = { module(this) })
            .start(wait = false)
    }

    @Subscribe
    private fun onPlayerDisconnect(event: DisconnectEvent) {
        sessionManager.deleteByValue(event.player.uniqueId)
    }

    /**
     * Configure application.
     */
    private fun module(app: Application) {
        app.install(ContentNegotiation) { json() }
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
                providerLookup = { config.oauthConfig.toOAuthServerSettings(sessionManager) }
                urlProvider = { config.oauthConfig.callback }
                client = HttpClient(Apache)
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
                    val playerUuid = principal?.state?.let { it1 -> sessionManager.getStateAndRemove(it1) }
                    call.respond("Player UUID: $playerUuid")
                    // TODO: Store the connection between player UUID and player
                }
            }
        }
    }
}
