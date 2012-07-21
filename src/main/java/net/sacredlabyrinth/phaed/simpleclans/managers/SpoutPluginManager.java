package net.sacredlabyrinth.phaed.simpleclans.managers;

import java.util.List;
import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import static org.getspout.spoutapi.SpoutManager.*;
import org.getspout.spoutapi.gui.*;
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

    private void sendInfo(SpoutPlayer sp, String text, float size, Color color, long duration)
    {

        final Screen screen = sp.getMainScreen();
        final Label info = new GenericLabel(text);

        if (color != null) {
            info.setTextColor(color);
        }

        info.setAlign(WidgetAnchor.CENTER_CENTER);
        info.setAnchor(WidgetAnchor.CENTER_CENTER);
        info.setScale(size);
        info.setWidth(30);
        info.setHeight(10);
        info.shiftXPos(-15);
        info.shiftYPos(-5);
        info.animate(WidgetAnim.POS_Y, 1.2F, (short) duration, (short) 1, false, false).animateStart();
        screen.attachWidget(plugin, info);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {

            @Override
            public void run()
            {
                screen.removeWidget(info);
            }
        }, duration);
    }

    public void enterClanRegion(SpoutPlayer sp, String tag)
    {
        sendInfo(sp, ChatColor.GRAY + tag, 2.6F, null, 35L);
    }

    public void leaveClanRegion(SpoutPlayer sp)
    {
        sendInfo(sp, ChatColor.DARK_GREEN + "Wilderness", 2.6F, null, 35L);
    }

    private class UpdateLocationInfo implements Runnable
    {

        @Override
        public void run()
        {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(player.getName());

                if (cp == null || !cp.isClanViewSettedUp()) {
                    return;
                }

                Location loc = player.getLocation();
                ChunkLocation chunk = new ChunkLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockZ(), true);
                Clan clanHere = plugin.getClanManager().getClanAt(chunk);
                boolean homeblock = clanHere == null ? false : clanHere.getHomeChunk().equals(chunk);

                StringBuilder sb = new StringBuilder();
                if (clanHere == null) {
                    sb.append(ChatColor.DARK_GREEN).append(plugin.getLang("wilderness"));
                } else {
                    sb.append(clanHere.getTag());
                    if (homeblock) {
                        sb.append(" (").append(plugin.getLang("homeblock")).append(')');
                    }
                }

                cp.updateClanView(sb.toString());
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
                        try {
                            sp.setCape(clan.getCapeUrl());
                        } catch (UnsupportedOperationException ex) {
                            SimpleClans.debug("Failed at parsing the cape url for clan " + clan.getName() + " (" + ex.getMessage() + ")");
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
