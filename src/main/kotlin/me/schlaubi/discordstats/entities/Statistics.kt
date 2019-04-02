package me.schlaubi.discordstats.entities

/**
 * Data container for bot statistics.
 * @param guildCount the exact count of guilds
 * @param shardIds all shard ids running on this instance
 * @param shardGuildCounts an array containing all guild counts per shard
 * @constructor Constructs a new Statistics object
 */
data class Statistics(
    val guildCount: Int,
    val shardIds: Array<Int>?,
    val shardGuildCounts: Array<Int>?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Statistics

        if (guildCount != other.guildCount) return false
        if (shardIds != null) {
            if (other.shardIds == null) return false
            if (!shardIds.contentEquals(other.shardIds)) return false
        } else if (other.shardIds != null) return false
        if (shardGuildCounts != null) {
            if (other.shardGuildCounts == null) return false
            if (!shardGuildCounts.contentEquals(other.shardGuildCounts)) return false
        } else if (other.shardGuildCounts != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = guildCount
        result = 31 * result + (shardIds?.contentHashCode() ?: 0)
        result = 31 * result + (shardGuildCounts?.contentHashCode() ?: 0)
        return result
    }
}