package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.entity.Player;
import org.getspout.spout.Spout;
import org.getspout.spoutapi.player.AppearanceManager;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundManager;

/**
 *
 * @author phaed
 */
public final class SpoutManager
{
    private SimpleClans plugin;
    private Spout spout;
    private AppearanceManager am;
    private SoundManager sm;

    /**
     *
     * @param plugin
     */
    public SpoutManager()
    {
        plugin = SimpleClans.getInstance();
    }

    /**
     * Process all players
     */
    public void processAllPlayers()
    {
        Player[] onlinePlayers = plugin.getServer().getOnlinePlayers();

        for (Player player : onlinePlayers)
        {
            processPlayer(player.getName());
        }
    }

    /**
     * Adds cape and title to a player
     * @param playerName
     */
    public void processPlayer(String playerName)
    {
        if (spout == null || am == null)
        {
            return;
        }

        Player player = Helper.matchOnePlayer(playerName);

        if (player != null)
        {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
            Clan clan = plugin.getClanManager().getClan(player);

            if (clan != null && cp != null && plugin.getClanManager().isVerified(clan))
            {
                if (plugin.getSettingsManager().isClanCapes())
                {
                    if (!clan.getCapeUrl().isEmpty())
                    {
                        am.setGlobalCloak(player, clan.getCapeUrl());
                    }
                    else
                    {
                        am.setGlobalCloak(player, plugin.getSettingsManager().getDefaultCapeUrl());
                    }
                }
                if (plugin.getSettingsManager().isInGameTags())
                {
                    am.setGlobalTitle(player, Helper.parseColors(clan.getTag()) + plugin.getSettingsManager().getTagSeparator() + player.getName());
                }
            }
        }
    }

    /**
     * Plays alert to player
     * @param player
     */
    public void playAlert(Player player)
    {
        if (sm != null)
        {
            SpoutPlayer sp = org.getspout.spoutapi.SpoutManager.getPlayerFromId(player.getEntityId());
            sm.playCustomSoundEffect(plugin, sp, plugin.getSettingsManager().getAlertUrl(), true);
        }
    }
}
