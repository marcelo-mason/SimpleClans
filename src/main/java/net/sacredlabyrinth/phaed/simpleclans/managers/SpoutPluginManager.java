package net.sacredlabyrinth.phaed.simpleclans.managers;

import java.util.List;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import static org.getspout.spoutapi.SpoutManager.*;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.player.SpoutPlayer;

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
    public SpoutPluginManager(SimpleClans plugin)
    {
        this.plugin = plugin;
        hasSpout = checkSpout();

        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new UpdateLocationInfo(), 20L, 40L);
    }

    /**
     * Process all players
     */
    public void processAllPlayers()
    {
        if (isHasSpout()) {
            Player[] onlinePlayers = plugin.getServer().getOnlinePlayers();

            for (Player player : onlinePlayers) {
                processPlayer(player);
            }
        }
    }

    private class UpdateLocationInfo implements Runnable
    {

        @Override
        public void run()
        {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp == null) {
                    return;
                }

                Location loc = player.getLocation();
                Clan clanHere = plugin.getClanManager().getClaimedClan(loc.getWorld(), loc.getBlockX(), loc.getBlockZ());
                String text = "§7" + (clanHere == null ? "No Clan here" : clanHere.getName());

                cp.updateClanView(text);
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
        if (isHasSpout()) {
            Player player = Helper.matchOnePlayer(playerName);

            if (player != null) {
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
        if (isHasSpout()) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null && cp.getClan().isVerified()) {
                Clan clan = cp.getClan();

                SpoutPlayer sp = getPlayer(player);

                if (plugin.getSettingsManager().isClanCapes()) {
                    if (!clan.getCapeUrl().isEmpty()) {
                        if (clan.getCapeUrl().toLowerCase().contains(".png")) {
                            sp.setCape(clan.getCapeUrl());
                        }
                    } else {
                        if (plugin.getSettingsManager().getDefaultCapeUrl().toLowerCase().contains(".png")) {
                            sp.setCape(plugin.getSettingsManager().getDefaultCapeUrl());
                        }
                    }
                }

                if (plugin.getSettingsManager().isInGameTags()) {
                    if (player.isSneaking()) {
                        sp.setTitle(player.getName());
                    } else {
                        String tag = plugin.getSettingsManager().isInGameTagsColored() ? (plugin.getSettingsManager().getTagBracketColor() + plugin.getSettingsManager().getTagBracketLeft() + clan.getColorTag() + plugin.getSettingsManager().getTagBracketColor() + plugin.getSettingsManager().getTagBracketRight() + plugin.getSettingsManager().getTagSeparatorColor() + plugin.getSettingsManager().getTagSeparator()) : ChatColor.DARK_GRAY + plugin.getSettingsManager().getTagBracketLeft() + clan.getTag() + plugin.getSettingsManager().getTagBracketRight() + plugin.getSettingsManager().getTagSeparator();
                        sp.setTitle(tag + ChatColor.WHITE + player.getName());
                    }
                }
            }
        }
    }

    public void showClanPlayers(Player player)
    {
        SpoutPlayer sp = getPlayer(player);
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        StringBuilder member = new StringBuilder();
        List<ClanPlayer> members = cp.getClan().getOnlineMembers();

        for (ClanPlayer cps : members) {
            member.append(cps.getName()).append("\n");
        }

        Label memberlabel = new GenericLabel(member.toString());
        memberlabel.setAutoDirty(true);
        memberlabel.setX(0);
        memberlabel.setY(0);

        sp.getMainScreen().attachWidget(plugin, memberlabel);

        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
        {

            @Override
            public void run()
            {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }, 0L, 2000L);
    }

    /**
     * Clear a player's cape
     *
     * @param player
     */
    public void clearCape(Player player)
    {
        if (isHasSpout()) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null && cp.getClan().isVerified()) {
                SpoutPlayer sp = getPlayer(player);
                sp.setCape("");
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
        if (isHasSpout()) {
            SpoutPlayer sp = getPlayerFromId(player.getEntityId());
            getSoundManager().playCustomSoundEffect(plugin, sp, plugin.getSettingsManager().getAlertUrl(), true);
        }
    }

    private boolean checkSpout()
    {
        Plugin test = plugin.getServer().getPluginManager().getPlugin("Spout");

        if (test != null) {
            SimpleClans.log(plugin.getLang("spout.features.enabled"));
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
