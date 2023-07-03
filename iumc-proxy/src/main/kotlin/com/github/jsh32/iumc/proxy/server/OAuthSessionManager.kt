package com.github.jsh32.iumc.proxy.server

import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Class that manages OAuth states
 */
class OAuthSessionManager<T> {
    private val preStateSessions = ConcurrentHashMap<UUID, T>()
    private val stateSessions = ConcurrentHashMap<String, T>()

    /**
     * Creates a session prior to generation of a state and returns a URL constructed
     * with the session ID. This URL includes query parameter session=sessionId and
     * should be used to initiate OAuth flow.
     */
    fun createPreStateSession(value: T, address: String): String {
        val sessionId = UUID.randomUUID()
        preStateSessions[sessionId] = value
        return "${address}/auth/login?session=${sessionId}"
    }

    /**
     * Upgrades the session to a state by removing it from preStateSessions status and
     * adding it to the stateSessions status with associated state identifier.
     * Returns whether operation was successful.
     */
    fun upgradePreStateSessionToState(sessionId: UUID, state: String): Boolean {
        val stateValue = preStateSessions.remove(sessionId)
        return if (stateValue != null) {
            stateSessions[state] = stateValue
            true
        } else {
            false
        }
    }

    /**
     * Given a state string, it fetches the associated value, if exists, and
     * removes it from the stateSessions map.
     */
    fun getStateAndRemove(state: String): T? {
        return stateSessions.remove(state)
    }

    /**
     * Deletes all sessions (prestate and state) associated with the given value.
     */
    fun deleteByValue(value: T) {
        preStateSessions.values.removeIf { it == value }
        stateSessions.values.removeIf { it == value }
    }
}