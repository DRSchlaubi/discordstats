package me.schlaubi.discordstats.entities.defaults.lists

import me.schlaubi.discordstats.DiscordStats
import me.schlaubi.discordstats.entities.Statistics
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

/**
 * Wrapper for [discordbots.org](https://discordbots.org/api/docs#bots).
 * @param token the API token for discordbots.org
 */
@Suppress("unused")
class DiscordBotsOrg(
    token: String
): TokenBasedBotlist(token) {

    companion object {
        const val API_URL = "https://discordbots.org/api"
        const val ENDPOINT_URL = "/bots/%s/stats"
    }

    override fun buildRequest(stats: Statistics, wrapper: DiscordStats): Request {
        val media = MediaType.parse("application/json")
        val json = JSONObject()
        if (wrapper.shardMode()) {
            if (stats.shardGuildCounts == null) {
                throw IllegalStateException("Statistics provider provided single-shard stats when running in shard mode")
            }
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