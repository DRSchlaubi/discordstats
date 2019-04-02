package me.schlaubi.discordstats;

import me.schlaubi.discordstats.entities.Statistics;
import me.schlaubi.discordstats.entities.BotList;
import me.schlaubi.discordstats.entities.StatsProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Discord stats main class.
 */
@SuppressWarnings("unused")
public interface DiscordStats {

    /**
     * Returns the current sending interval.
     * @return the interval
     */
    @NotNull
    long getInterval();

    /**
     * Returns the current scheduler
     * @return the scheduler {@link ScheduledExecutorService}
     */
    @NotNull
    ScheduledExecutorService getScheduler();

    /**
     * Returns an immutable list containing all registered botlists.
     * @return a list {@link List} containing all botlists
     */
    @NotNull
    List<BotList> getBotLists();

    /**
     * Returns whether the bot uses shard request (if supported by bot list) or not.
     * @return whether the shard mode is enabled or not
     */
    @NotNull
    boolean shardMode();

    /**
     * Returns the date of the last post.
     * @return the {@link OffsetDateTime}
     */
    @Nullable
    OffsetDateTime getLastPost();

    /**
     * Returns the last sent statistics object.
     * @return the {@link Statistics} object
     */
    @Nullable
    Statistics getLastSentStatistics();

    /**
     * Executes an new post for all botlists.
     * @return A {@link CompletionStage} that completes when all requests are finished
     */
    @NotNull
    // TODO: 02.04.19 Add support for only sending request to a specific botlist
    CompletionStage<Void> post();

    /**
     * Returns the current stats provider.
     * @return the {@link StatsProvider}
     */
    @NotNull
    StatsProvider getStatsProvider();

    /**
     * Stops the sending interval.
     */
    void stopLoop();

    /**
     * Starts the sending interval.
     */
    void startLoop();
}
