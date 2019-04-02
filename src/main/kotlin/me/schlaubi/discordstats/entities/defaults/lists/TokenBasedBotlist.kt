package me.schlaubi.discordstats.entities.defaults.lists

import me.schlaubi.discordstats.entities.BotList
import okhttp3.Request
import okhttp3.RequestBody

/**
 * Internally used helper class.
 */
abstract class TokenBasedBotlist(
    private val token: String
) : BotList() {

    protected fun buildRequest(
        base: String,
        endpoint: String,
        botId: Long,
        body: RequestBody,
        authHeader: String = AUTHORIZATION_HEADER
    ): Request {
        return Request.Builder()
            .url(formatUrl(base, endpoint, botId))
            .post(body)
            .addHeader(authHeader, token)
            .build()
    }
}