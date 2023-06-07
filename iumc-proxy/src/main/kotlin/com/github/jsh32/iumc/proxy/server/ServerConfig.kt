package com.github.jsh32.iumc.proxy.server

import io.ktor.http.*
import io.ktor.server.auth.*
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
class ServerConfig(
    @Comment("Port to run server under")
    val port: Int = 3000,
    @Comment("Host to run server under")
    val host: String = "0.0.0.0",
    @Comment("OAuth server config")
    val oauthConfig: OAuthConfig = OAuthConfig()
)

@ConfigSerializable
class OAuthConfig(
    @Comment("OAuth server address")
    val address: String = "",
    @Comment("Callback URL for OAuth")
    val callback: String = "",
    private val authorizeUrl: String = "",
    private val accessTokenUrl: String = "",
    private val clientId: String = "",
    private val clientSecret: String = ""
) {
    fun toOAuthServerSettings() = OAuthServerSettings.OAuth2ServerSettings(
        name = "IU Login",
        authorizeUrl = this.authorizeUrl,
        accessTokenUrl = this.accessTokenUrl,
        clientId = this.clientId,
        clientSecret = this.clientSecret,
        accessTokenRequiresBasicAuth = false,
        requestMethod = HttpMethod.Post, // must POST to token endpoint
        defaultScopes = listOf("openid", "profile")
    )
}
