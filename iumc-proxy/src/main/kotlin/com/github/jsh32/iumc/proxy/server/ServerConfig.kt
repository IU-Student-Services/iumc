package com.github.jsh32.iumc.proxy.server

import com.velocitypowered.api.proxy.Player
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment
import java.util.*

@ConfigSerializable
class ServerConfig(
    @Comment("Public facing IP of the server")
    val publicIp: String = "example.com",
    @Comment("Server root address")
    val address: String = "",
    @Comment("Port to run server on")
    val port: Int = 3000,
    @Comment("Host to run server on")
    val host: String = "0.0.0.0",
    @Comment("OAuth server config. IU production URLs are set for defaults")
    val oauthConfig: OAuthConfig = OAuthConfig()
)

@ConfigSerializable
class OAuthConfig(
    @Comment("Callback URL for OAuth")
    val callback: String = "",
    @Comment("Userinfo route")
    val userInfo: String = "https://idp.login.iu.edu/idp/profile/oidc/userinfo",
    private val authorizeUrl: String = "https://idp.login.iu.edu/idp/profile/oidc/authorize",
    private val accessTokenUrl: String = "https://idp.login.iu.edu/idp/profile/oidc/token",
    private val clientId: String = "",
    private val clientSecret: String = ""
) {
    fun toOAuthServerSettings(sessionManager: OAuthSessionManager<Any>) = OAuthServerSettings.OAuth2ServerSettings(
        name = "IU Login",
        authorizeUrl = this.authorizeUrl,
        accessTokenUrl = this.accessTokenUrl,
        clientId = this.clientId,
        clientSecret = this.clientSecret,
        accessTokenRequiresBasicAuth = true,
        requestMethod = HttpMethod.Post, // must POST to token endpoint
        defaultScopes = listOf("openid", "profile"),
        onStateCreated = { call, state ->
            call.parameters["session"]
                ?.let { sessionId ->
                    runCatching { UUID.fromString(sessionId) }
                        .onFailure { throw BadRequestException("Invalid UUID format in session.") }
                        .onSuccess { session ->
                            if (!sessionManager.upgradePreStateSessionToState(session, state)) {
                                throw BadRequestException("Invalid session provided.")
                            }
                        }
                }
                ?: throw MissingRequestParameterException("session")
        }
    )
}
