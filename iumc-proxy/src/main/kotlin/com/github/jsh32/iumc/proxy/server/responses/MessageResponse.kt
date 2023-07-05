package com.github.jsh32.iumc.proxy.server.responses

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

@Serializable
class MessageResponse(
    val statusCode: Int,
    val message: String
)
