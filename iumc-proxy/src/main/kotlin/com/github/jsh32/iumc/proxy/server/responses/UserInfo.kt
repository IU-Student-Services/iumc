package com.github.jsh32.iumc.proxy.server.responses

import kotlinx.serialization.Serializable

/**
 * Userinfo response from IU-Login
 */
@Serializable
data class UserInfo(
    /**
     * Subject, or the unique account identifier, should be users email
     */
    val sub: String,
    /**
     * Users display name
     */
    val displayName: String,
    /**
     * First name
     */
    val givenName: String,
    /**
     * Should be the users email
     */
    val eduPersonPrincipalName: String,
    /**
     * Last name
     */
    val sn: String,
    /**
     * Username
     */
    val username: String,
)