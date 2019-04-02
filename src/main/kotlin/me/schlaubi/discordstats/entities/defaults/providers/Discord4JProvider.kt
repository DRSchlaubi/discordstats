package me.schlaubi.discordstats.entities.defaults.providers

import discord4j.core.DiscordClient
import discord4j.core.`object`.util.Snowflake
import me.schlaubi.discordstats.entities.defaults.GenericProvider

@Suppress("unused")
class Discord4JProvider : GenericProvider {

    constructor(discordClient: DiscordClient) : super(
        {
            discordClient.guilds.count().block()?.toInt()
        },
        discordClient.selfId.orElse(Snowflake.of(-1)).asLong()
    )
}