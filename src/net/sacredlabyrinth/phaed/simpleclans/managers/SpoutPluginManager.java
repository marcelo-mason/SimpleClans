package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.util.logging.Level;

/**
 * @author phaed
 */
public final class SpoutPluginManager
{
    private SimpleClans plugin;
    private boolean hasSpout;

    /**
     *
     */
    public SpoutPluginManager()
    {
        plugin = SimpleClans.getInstance();
        hasSpout = checkSpout();
    }

    /**
     * Process all players
     */
    public void processAllPlayers()
    {
        if (isHasSpout())
        {
            Player[] onlinePlayers = plugin.getServer().getOnlinePlayers();

            for (Player player : onlinePlayers)
            {
                processPlayer(player);
            }
        }
    }

    /**
     * Adds cape and title to a player
     *
     * @param playerName
     */
    public void processPlayer(String playerName)
    {
        if (isHasSpout())
        {
            Player player = Helper.matchOnePlayer(playerName);

            if (player != null)
            {
                processPlayer(player);
            }
        }
    }

    /**
     * Adds cape and title to a player
     *
     * @param player the player
     */
    public void processPlayer(Player player)
    {
        if (isHasSpout())
        {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null && cp.getClan().isVerified())
            {
                Clan clan = cp.getClan();

                if (plugin.getSettingsManager().isClanCapes())
                {
                    if (!clan.getCapeUrl().isEmpty())
                    {
                        SpoutManager.getAppearanceManager().setGlobalCloak(player, clan.getCapeUrl());
                    }
                    else
                    {
                        SpoutManager.getAppearanceManager().setGlobalCloak(player, plugin.getSettingsManager().getDefaultCapeUrl());
                    }
                }

                if (plugin.getSettingsManager().isInGameTags())
                {
                    if (player.isSneaking())
                    {
                        SpoutManager.getAppearanceManager().setGlobalTitle(player, player.getName());
                    }
                    else
                    {
                        String tag = plugin.getSettingsManager().isInGameTagsColored() ? (plugin.getSettingsManager().getTagBracketColor() + plugin.getSettingsManager().getTagBracketLeft() + clan.getColorTag() + plugin.getSettingsManager().getTagBracketColor() + plugin.getSettingsManager().getTagBracketRight() + plugin.getSettingsManager().getTagSeparatorColor() + plugin.getSettingsManager().getTagSeparator()) : ChatColor.DARK_GRAY + plugin.getSettingsManager().getTagBracketLeft() + clan.getTag() + plugin.getSettingsManager().getTagBracketRight() + plugin.getSettingsManager().getTagSeparator();
                        SpoutManager.getAppearanceManager().setGlobalTitle(player, tag + ChatColor.WHITE + player.getName());
                    }
                }
            }
        }
    }

    /**
     * Plays alert to player
     *
     * @param player
     */
    public void playAlert(Player player)
    {
        if (isHasSpout())
        {
            SpoutPlayer sp = org.getspout.spoutapi.SpoutManager.getPlayerFromId(player.getEntityId());
            SpoutManager.getSoundManager().playCustomSoundEffect(plugin, sp, plugin.getSettingsManager().getAlertUrl(), true);
        }
    }

    private boolean checkSpout()
    {
        Plugin test = plugin.getServer().getPluginManager().getPlugin("Spout");

        if (test != null)
        {
            SimpleClans.log(Level.INFO, plugin.getLang().getString("spout.features.enabled"));
            return true;
        }
        return false;
    }

    /**
     * @return the hasSpout
     */
    public boolean isHasSpout()
    {
        return hasSpout;
    }
}
