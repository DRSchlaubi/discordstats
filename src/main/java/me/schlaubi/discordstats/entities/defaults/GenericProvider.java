package me.schlaubi.discordstats.entities.defaults;

import me.schlaubi.discordstats.entities.Statistics;
import me.schlaubi.discordstats.entities.StatsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Internally used helper class.
 */
@SuppressWarnings("unused")
public abstract class GenericProvider implements StatsProvider {

    private final transient Supplier<Integer> guildCount;
    private final transient Supplier<Integer[]> shardIds;
    private final transient Supplier<Integer[]> shardGuildCounts;
    private final long botId;

    @SuppressWarnings("WeakerAccess")
    public GenericProvider(
            @NotNull Supplier<Integer> guildCount,
            @NotNull Supplier<Integer[]> shardIds,
            @NotNull Supplier<Integer[]> shardGuildCounts,
            long botId) {
        this.guildCount = guildCount;
        this.shardIds = shardIds;
        this.shardGuildCounts = shardGuildCounts;
        this.botId = botId;
    }

    public GenericProvider(@NotNull Supplier<Integer> guildCount, long botId) {
        this(guildCount, () -> null, ()-> null, botId);
    }

    @NotNull
    @Override
    public Statistics buildStatistics() {
        return new Statistics(
                guildCount.get(),
                shardIds.get(),
                shardGuildCounts.get()
        );
    }

    @Override
    public long getBotId() {
        return botId;
    }
}
