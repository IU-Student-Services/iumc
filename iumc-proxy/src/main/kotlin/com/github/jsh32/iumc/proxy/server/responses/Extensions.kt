package com.github.jsh32.iumc.proxy.server.responses

import com.github.jsh32.iumc.proxy.Globals
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*

suspend fun ApplicationCall.respondFtl(template: String, params: Map<out String, Any>) =
    respond(
        FreeMarkerContent(
            template,
            mutableMapOf(
                "versionHash" to Globals.VERSION_HASH
            ) + params
        )
    )