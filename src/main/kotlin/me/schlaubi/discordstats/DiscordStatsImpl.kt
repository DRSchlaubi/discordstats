package me.schlaubi.discordstats

import me.schlaubi.discordstats.entities.Statistics
import me.schlaubi.discordstats.entities.BotList
import me.schlaubi.discordstats.entities.StatsProvider
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import java.time.OffsetDateTime
import java.util.concurrent.*
import java.util.function.Consumer

/**
 * Implementation of [DiscordStats].
 * @see me.schlaubi.discordstats.builder.DiscordStatsBuilder
 */
class DiscordStatsImpl(
    private val scheduler: ScheduledExecutorService,
    private val interval: Long,
    private val initialDelay: Long,
    private val lists: List<BotList>,
    private val shardMode: Boolean,
    private val provider: StatsProvider,
    private val errorHandler: Consumer<IOException>,
    private val successHandler: Consumer<Response>,
    private val client: OkHttpClient
) : DiscordStats {

    private var lastPost: OffsetDateTime? = null
    private var lastSentStatistics: Statistics? = null
    private lateinit var future: ScheduledFuture<*>

    init {
        if (interval != -1L) {
            startLoop()
        }
    }

    override fun post(): CompletionStage<Void> {
        // Get stats
        val stats = provider.buildStatistics()
        // Build requests
        val futures = lists.map {
            it.buildRequest(stats, this)
        }.map {
            // Build calls
            client.newCall(it)
        }.map {
            val future = CompletableFuture<Response>()
            it.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    future.completeExceptionally(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    future.complete(null)
                }

            })
            // Handlers
            future.thenAccept(successHandler)
            future.exceptionally { ex ->
                errorHandler.accept(ex as IOException)
                null
            }
            future
        }
        lastPost = OffsetDateTime.now()
        lastSentStatistics = stats
        return CompletableFuture.allOf(*futures.toTypedArray())
    }

    override fun stopLoop() {
        if (!this::future.isInitialized || this.future.isCancelled) {
            throw IllegalStateException("Loop is already stopped.")
        }
        future.cancel(true)
    }

    override fun startLoop() {
        if (this::future.isInitialized && !this.future.isCancelled && !this.future.isDone) {
            throw IllegalStateException("Loop is already running.")
        }
        future = scheduler.scheduleAtFixedRate({
            post()
        }, initialDelay, interval, TimeUnit.MILLISECONDS)
    }

    override fun getScheduler(): ScheduledExecutorService {
        return scheduler
    }

    override fun getInterval(): Long {
        return interval
    }

    override fun getBotLists(): List<BotList> {
        return lists
    }

    override fun shardMode(): Boolean {
        return shardMode
    }

    override fun getStatsProvider(): StatsProvider {
        return provider
    }

    override fun getLastPost(): OffsetDateTime? {
        return lastPost
    }

    override fun getLastSentStatistics(): Statistics? {
        return lastSentStatistics
    }
}