package net.sacredlabyrinth.phaed.simpleclans.managers;

import java.util.HashMap;
import java.util.Map;
import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import static org.getspout.spoutapi.SpoutManager.getPlayer;
import static org.getspout.spoutapi.SpoutManager.getSoundManager;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.gui.*;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author phaed
 */
public class SpoutPluginManager implements Listener
{

    private SimpleClans plugin;
    private Map<String, Label> claimViews = new HashMap<String, Label>();

    /**
     *
     */
    public SpoutPluginManager(SimpleClans plugin)
    {
        this.plugin = plugin;
        if (plugin.getSettingsManager().isClaimingEnabled()) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
        if (plugin.getSettingsManager().isClaimingEnabled()) {
            plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new UpdateLocationInfo(), 20L, 40L);
        }
    }

    @EventHandler
    public void onSpoutCraftCreate(SpoutCraftEnableEvent event)
    {
        if (plugin.getSettingsManager().isClaimingEnabled()) {
            SpoutPlayer sp = event.getPlayer();
            ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(sp.getName());
            if (cp != null) {
                Clan clan = cp.getClan();
                plugin.getSpoutPluginManager().setupClaimView(sp);
                if (clan != null) {
                    Location home = clan.getHomeChunkMiddle();
                    if (home != null) {
                        double x = home.getX();
                        double z = home.getZ();
                        double y = home.getY();

                        sp.addWaypoint("Homeblock", x, y, z);
                    }
                }
            }
        }
    }

    /**
     * Process all players
     */
    public void processAllPlayers()
    {

        Player[] onlinePlayers = plugin.getServer().getOnlinePlayers();

        for (Player player : onlinePlayers) {
            processPlayer(player);
        }

    }

    public final void setupClaimView(ClanPlayer cp)
    {
        setupClaimView(getPlayer(cp.toPlayer()));
    }

    public final void setupClaimView(SpoutPlayer sp)
    {
        String name = sp.getName();
        if (!hasClaimView(name)) {
            Screen screen = sp.getMainScreen();
            Label claimView = new GenericLabel("");
            claimView.setShadow(true).setAlign(WidgetAnchor.TOP_RIGHT);
            claimView.setWidth(20).setHeight(10).setX(screen.getWidth() - claimView.getWidth() - 5).setY(5).setAutoDirty(true).setDirty(true);
            claimViews.put(name, claimView);
            screen.attachWidget(plugin, claimView);
        }
    }

    public void removeClaimView(Player player)
    {
        String name = player.getName();
        if (hasClaimView(name)) {

            getPlayer(player).getMainScreen().removeWidget(getClaimView(name));
            claimViews.remove(name);
        }
    }

    public void updateClaimView(String player, String text)
    {
        Label label = getClaimView(player);
        if (hasClaimView(player) && !text.equals(label.getText())) {
            label.setText(text);
        }
    }

    public Label getClaimView(String player)
    {
        return claimViews.get(player);
    }

    public boolean hasClaimView(String player)
    {
        return claimViews.containsKey(player);
    }

    public void sendInfo(SpoutPlayer sp, String text, float size, Color color, long duration)
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

                if (cp == null) {
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

                updateClaimView(cp.getName(), sb.toString());
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
        Player player = Helper.matchOnePlayer(playerName);

        if (player != null) {
            processPlayer(player);
        }
    }

    /**
     * Adds cape and title to a player
     *
     * @param player the player
     */
    public void processPlayer(Player player)
    {
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

//    public void showClanPlayers(Player player)
//    {
//        SpoutPlayer sp = getPlayer(player);
//        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
//
//        StringBuilder member = new StringBuilder();
//        List<ClanPlayer> members = cp.getClan().getOnlineMembers();
//
//        for (ClanPlayer cps : members) {
//            member.append(cps.getName()).append("\n");
//        }
//
//        Label memberlabel = new GenericLabel(member.toString());
//        memberlabel.setAutoDirty(true);
//        memberlabel.setX(0);
//        memberlabel.setY(0);
//
//        sp.getMainScreen().attachWidget(plugin, memberlabel);
//
//        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
//        {
//
//            @Override
//            public void run()
//            {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//        }, 0L, 2000L);
//    }
    /**
     * Clear a player's cape
     *
     * @param player
     */
    public void clearCape(Player player)
    {
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp != null && cp.getClan().isVerified()) {
            SpoutPlayer sp = getPlayer(player);
            sp.setCape("");
        }
    }

    /**
     * Plays alert to player
     *
     * @param player
     */
    public void playAlert(Player player)
    {
        SpoutPlayer sp = getPlayer(player);
        getSoundManager().playCustomSoundEffect(plugin, sp, plugin.getSettingsManager().getAlertUrl(), true);
    }
}
