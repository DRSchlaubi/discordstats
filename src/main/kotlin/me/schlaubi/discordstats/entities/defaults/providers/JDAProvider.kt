package me.schlaubi.discordstats.entities.defaults.providers

import me.schlaubi.discordstats.entities.defaults.GenericProvider
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.sharding.ShardManager

/**
 * Default implementation of [me.schlaubi.discordstats.entities.StatsProvider] for JDA users.
 */
@Suppress("unused")
class JDAProvider: GenericProvider {

    /**
     * Constructor for one-shard users.
     * @param jda your jda instance
     */
    constructor(jda: JDA): super(
        {
            jda.guildCache.size().toInt()
        },
        {
            null
        },
        {
            null
        },
        jda.selfUser.idLong
    )

    /**
     * Constructor for sharded users
     * @param shardManager your shard manager
     */
    constructor(shardManager: ShardManager): super(
        {
            shardManager.guildCache.size().toInt()
        },
        {
            shardManager.shards.map { it.shardInfo.shardId }.toTypedArray()
        },
        {
            shardManager.shards.map { it.guildCache.size().toInt() }.toTypedArray()
        },
        shardManager.retrieveApplicationInfo().complete().idLong
    )
}