package com.github.jsh32.iumc.proxy.server

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class IUMCApplication(val config: ServerConfig) {
    init {
        embeddedServer(Netty, port = config.port, host = config.host, module = { module(this) })
            .start(wait = true)
    }

    /**
     * Configure application.
     */
    private fun module(app: Application) {
        app.install(ContentNegotiation) {
            json()
        }

        app.install(Authentication) {
            oauth("iu-login") {
                providerLookup = { config.oauthConfig.toOAuthServerSettings() }
                urlProvider = { config.oauthConfig.callback }
            }
        }

        app.routing {
        }
    }
}
