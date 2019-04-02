package me.schlaubi.discordstats.builder;

import com.google.common.base.Preconditions;
import me.schlaubi.discordstats.DiscordStats;
import me.schlaubi.discordstats.DiscordStatsImpl;
import me.schlaubi.discordstats.entities.BotList;
import me.schlaubi.discordstats.entities.StatsProvider;
import me.schlaubi.discordstats.util.NameableThreadFactory;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Builder pattern for {@link DiscordStats}.
 */
@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
public class DiscordStatsBuilder {

    private static final transient Logger log = LoggerFactory.getLogger(DiscordStatsImpl.class);

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new NameableThreadFactory("DiscordStatsScheduler"));
    private long interval = TimeUnit.MINUTES.toMillis(10);
    private long initialDelay = interval;
    private List<BotList> botLists = new ArrayList<>();
    private boolean shardMode = false;
    private StatsProvider provider = null;
    private Consumer<IOException> errorHandler =  e -> log.error("[DiscordStats] Could not post stats to botlist", e);
    private Consumer<Response> successHandler = r -> log.debug("[DiscordStats] Successfully posted stats to {}", r.request().url());
    private OkHttpClient okHttpClient = new OkHttpClient();

    /**
     * Convenience constructor for faster access.
     * @param provider the Stats provider
     * @param lists a collection containing all botlists
     */
    public DiscordStatsBuilder(@NotNull StatsProvider provider, @NotNull Collection<BotList> lists) {
        setProvider(provider);
        registerBotLists(lists);
    }

    /**
     * Default constructor.
     * Leave him alone Codacy okay?
     * okay!
     */
    public DiscordStatsBuilder() {
    }

    /**
     * Returns the current scheduler.
     * @return the scheduler {@link ScheduledExecutorService}
     */
    @NotNull
    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    @NotNull
    public DiscordStatsBuilder setScheduler(@NotNull ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    /**
     * Returns the current loop interval.
     * @return the loop
     */
    public long getInterval() {
        return interval;
    }

    /**
     * Disables the automatic start of the posting loop.
     * You can start it manually again by calling {@link DiscordStats#startLoop()} or send requests
     * by calling {@link DiscordStats#post()}
     * @return the current builder
     */
    @NotNull
    public DiscordStatsBuilder disableAutoStart() {
        return setInterval(-1);
    }

    /**
     * Sets the interval for posting loop.
     * @param interval the interval
     * @param unit the time unit of the interval
     * @return the current builder
     */
    @NotNull
    public DiscordStatsBuilder setInterval(long interval, TimeUnit unit) {
        return setInterval(unit.toMillis(interval));
    }

    /**
     * Sets the interval for posting loop.
     * @param interval the interval in milliseconds
     * @return the current builder
     */
    @NotNull
    public DiscordStatsBuilder setInterval(long interval) {
        Preconditions.checkArgument(interval >= -1, "Interval must be bigger than -1");
        this.interval = interval;
        return this;
    }

    /**
     * Returns the current initial delay.
     * @return the delay
     */
    public long getInitialDelay() {
        return initialDelay;
    }

    /**
     * Sets the initial delay (delay after starting the loop) of the loop.
     * @param initialDelay the initial delay
     * @param unit the time unit of the delay
     * @return the current builder
     */
    @NotNull
    public DiscordStatsBuilder setInitialDelay(long initialDelay, TimeUnit unit) {
        return setInterval(unit.toMillis(initialDelay));
    }

    /**
     * Sets the initial delay (delay after starting the loop) of the loop.
     * @param initialDelay the initial delay in milliseconds
     * @return the current builder
     */
    @NotNull
    public DiscordStatsBuilder setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
        return this;
    }

    /**
     * Returns the current list of registered botlists.
     * @return the list
     */
    @NotNull
    public List<BotList> getBotLists() {
        return botLists;
    }

    /**
     * Sets the list of registered botlists.
     * @param lists the list
     * @return the current builder
     */
    @NotNull
    public DiscordStatsBuilder setBotLists(@NotNull List<BotList> lists) {
        this.botLists = lists;
        return this;
    }

    /**
     * Registers all botlist from the specified collection.
     * @param lists the collection
     * @return the current builder
     */
    @NotNull
    public DiscordStatsBuilder registerBotLists(@NotNull Collection<BotList> lists) {
        this.botLists.addAll(lists);
        return this;
    }

    /**
     * Registers all botlist from the specified array.
     * @param lists the array
     * @return the current builder
     */
    @NotNull
    public DiscordStatsBuilder registerBotLists(@NotNull BotList... lists) {
        Collections.addAll(this.botLists, lists);
        return this;
    }

    /**
     * Returns whether the bot uses shard request (if supported by bot list) or not.
     * @return whether the shard mode is enabled or not
     */
    public boolean getShardMode() {
        return shardMode;
    }

    /**
     * Defines whether the HTTP client should send shard specific requests or normal requests.
     * @param shardMode boolean
     * @return the current builder
     */
    @NotNull
    public DiscordStatsBuilder setShardMode(boolean shardMode) {
        this.shardMode = shardMode;
        return this;
    }

    /**
     * Returns the current stats provider.
     * @return the {@link StatsProvider}
     */
    @Nullable
    public StatsProvider getProvider() {
        return provider;
    }

    /**
     * Sets the provider for bot statistics.
     * @param provider the {@link StatsProvider}
     * @return the current builder
     */
    @NotNull
    public DiscordStatsBuilder setProvider(@NotNull StatsProvider provider) {
        this.provider = provider;
        return this;
    }

    /**
     * Returns the current error handler.
     * @return the handler
     */
    @NotNull
    public Consumer<IOException> getErrorHandler() {
        return errorHandler;
    }

    /**
     * Disables standard log messages.
     * @return the current builder
     */
    @NotNull
    public DiscordStatsBuilder disableLogging() {
        disableErrorLogging();
        return disableSuccessLogging();
    }

    /**
     * Disables standard log messages on failed requests.
     * @return the current builder
     */
    @NotNull
    public DiscordStatsBuilder disableErrorLogging() {
        return setErrorHandler(r -> {});
    }

    /**
     * Registers a method which gets executed whenever a Request fails.
     * @param errorHandler the method
     * @return the current builder
     */
    @NotNull
    public DiscordStatsBuilder setErrorHandler(@NotNull Consumer<IOException> errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    /**
     * Returns the current success handler.
     * @return the success handler
     */
    @NotNull
    public Consumer<Response> getSuccessHandler() {
        return successHandler;
    }

    /**
     * Disables standard log messages on succeeded requests.
     * @return the current builder
     */
    @NotNull
    public DiscordStatsBuilder disableSuccessLogging() {
        return setSuccessHandler(r -> {});
    }

    /**
     * Registers a method which gets executed whenever a Request succeeded.
     * @param successHandler the method
     * @return the current builder
     */
    @NotNull
    public DiscordStatsBuilder setSuccessHandler(@NotNull Consumer<Response> successHandler) {
        this.successHandler = successHandler;
        return this;
    }

    /**
     * Returns the current ok http client.
     * @return the client
     */
    @NotNull
    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    /**
     * Sets the HTTP client used for requests to the botlists.
     * @param okHttpClient the {@link OkHttpClient}
     * @return the current builder
     */
    @NotNull
    public DiscordStatsBuilder setOkHttpClient(@NotNull OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
        return this;
    }

    /**
     * Sets the HTTP client used for requests to the botlists by its builder.
     * @param okHttpClientBuilder the {@link OkHttpClient.Builder}
     * @return the current builder
     */
    @NotNull
    public DiscordStatsBuilder setOkHttpClient(@NotNull OkHttpClient.Builder okHttpClientBuilder) {
        return setOkHttpClient(okHttpClientBuilder.build());
    }

    /**
     * Builds a new {@link DiscordStats} instance.
     * @throws IllegalArgumentException When no botlists are registred
     * @throws NullPointerException When {@link DiscordStatsBuilder#getProvider()} is null
     * @return the instance.
     */
    @SuppressWarnings("SpellCheckingInspection")
    @NotNull
    public DiscordStats build() {
        Preconditions.checkArgument(!botLists.isEmpty(), "Botlists cannot be empty!");
        Preconditions.checkNotNull(provider, "Statsprovider cannot be null!");
        
        return new DiscordStatsImpl(
                scheduler,
                interval,
                initialDelay,
                List.copyOf(botLists),
                shardMode,
                provider,
                errorHandler,
                successHandler,
                okHttpClient
        );
    }
}
