package me.schlaubi.discordstats.entities;

import discord4j.core.DiscordClient;
import me.schlaubi.discordstats.entities.defaults.providers.Discord4JProvider;
import me.schlaubi.discordstats.entities.defaults.providers.JDAProvider;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for providing bot statistics
 */
@SuppressWarnings("unused")
public interface StatsProvider {

    /**
     * Creates a new stats provider for JDA users.
     * @param jda your jda instance
     * @return the {@link StatsProvider}
     */
    static StatsProvider fromJDA(JDA jda) {
        return new JDAProvider(jda);
    }

    /**
     * Creates a new stats provider for sharded JDA users.
     * @param jda your jda shard manager instance
     * @return the {@link StatsProvider}
     */
    static StatsProvider fromJDA(ShardManager jda) {
        return new JDAProvider(jda);
    }

    /**
     * Creates a new stats provider for sharded D4J users.
     * @param discordClient your {@link DiscordClient} instance
     * @return the {@link StatsProvider}
     */
    static StatsProvider fromD4J(DiscordClient discordClient) {
        return new Discord4JProvider(discordClient);
    }

    /**
     * Method that should post statistics.
     * @see Statistics
     * @return the current statistics object
     */
    @NotNull
    Statistics buildStatistics();

    /**
     * Method that should return the bots id.
     * @return the id of the bot
     */
    long getBotId();
}
