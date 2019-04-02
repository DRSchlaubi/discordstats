package me.schlaubi.discordstats.entities;

import me.schlaubi.discordstats.DiscordStats;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for botlists.
 */
public abstract class BotList {

    /**
     * The default header for Authorization
     */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Method that should build a new request
     * @param stats the lates {@link Statistics} object
     * @param wrapper the current {@link DiscordStats} instance
     * @return the {@link Request}
     */
    public abstract Request buildRequest(@NotNull Statistics stats, @NotNull DiscordStats wrapper);

    /**
     * Formats a new url.
     * @param base the API base url
     * @param endpoint the endpoint url
     * @param botId the id of the bot
     * @return the formatted url
     */
    protected String formatUrl(String base, String endpoint, long botId) {
        return String.format(base + endpoint, botId);
    }
}
