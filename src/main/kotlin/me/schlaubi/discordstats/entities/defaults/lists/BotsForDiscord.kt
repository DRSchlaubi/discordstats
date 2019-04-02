package me.schlaubi.discordstats.entities.defaults.lists

import me.schlaubi.discordstats.DiscordStats
import me.schlaubi.discordstats.entities.Statistics
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import org.slf4j.LoggerFactory

/**
 * Wrapper for [botsfordiscord.com](https://docs.botsfordiscord.com/methods/bots#bot-stats).
 * @param token the API token for botsfordiscord.com
 */
@Suppress("unused")
class BotsForDiscord(
    token: String
): TokenBasedBotlist(token) {

    companion object {
        const val API_URL = "https://botsfordiscord.com/api"
        const val ENDPOINT_URL = "/bot/%s"
    }

    private val log = LoggerFactory.getLogger(BotsForDiscord::class.java)

    override fun buildRequest(stats: Statistics, wrapper: DiscordStats): Request {
        val media = MediaType.parse("application/json")
        val json = JSONObject()
        if (wrapper.shardMode()) {
            log.warn("[BotsForDiscord.com] BotsForDiscord does currently not support shard stats!")
        }
        json.put("server_count", stats.guildCount)
        val body = RequestBody.create(media, json.toString())
        return buildRequest(
            API_URL,
            ENDPOINT_URL,
            wrapper.statsProvider.botId,
            body
        )
    }

}