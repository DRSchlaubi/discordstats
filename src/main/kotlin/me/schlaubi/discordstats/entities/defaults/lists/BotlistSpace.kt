package me.schlaubi.discordstats.entities.defaults.lists

import me.schlaubi.discordstats.DiscordStats
import me.schlaubi.discordstats.entities.Statistics
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

/**
 * Wrapper for [botlist.space](https://docs.botlist.space/bl-docs/bots#server-count).
 * @param token the API token for botlist.space
 */
@Suppress("unused")
class BotlistSpace(
    token: String
): TokenBasedBotlist(token) {

    companion object {
        const val API_URL = "https://api.botlist.space/v1"
        const val ENDPOINT_URL = "/bots/%s"
    }

    override fun buildRequest(stats: Statistics, wrapper: DiscordStats): Request {
        val media = MediaType.parse("application/json")
        val json = JSONObject()
        if (wrapper.shardMode()) {
            json.put("shards", stats.shardGuildCounts)
        } else {
            json.put("server_count", stats.guildCount)
        }
        val body = RequestBody.create(media, json.toString())
        return buildRequest(
            API_URL,
            ENDPOINT_URL,
            wrapper.statsProvider.botId,
            body
        )
    }

}