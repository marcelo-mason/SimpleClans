package net.sacredlabyrinth.phaed.simpleclans.listeners;

import java.util.List;
import java.util.logging.Level;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 *
 * @author phaed
 */
public class SCPlayerListener extends PlayerListener
{
    private SimpleClans plugin;

    /**
     *
     */
    public SCPlayerListener()
    {
        plugin = SimpleClans.getInstance();
    }

    /**
     *
     * @param event
     */
    @Override
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        if (event.isCancelled())
        {
            return;
        }

        Player player = event.getPlayer();

        if (player == null)
        {
            return;
        }

        if (!plugin.getSettingsManager().getClanChatEnable())
        {
            return;
        }

        if (event.getMessage().length() == 0)
        {
            return;
        }

        String[] split = event.getMessage().substring(1).split(" ");

        if (split.length == 0)
        {
            return;
        }

        String command = split[0];
        String msg = Helper.toMessage(Helper.removeFirst(split));

        if (plugin.getClanManager().isClan(command))
        {
            Clan clan = plugin.getClanManager().getClan(command);

            if (!clan.isMember(player))
            {
                return;
            }

            announceClan(clan, player, msg);
            event.setCancelled(true);
        }
    }

    private void announceClan(Clan clan, Player player, String msg)
    {
        String message = plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketLeft() + plugin.getSettingsManager().getTagDefaultColor() + clan.getColorTag() + plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketRight() + " " + plugin.getSettingsManager().getClanChatNameColor() + plugin.getSettingsManager().getClanChatPlayerBracketLeft() + player.getName() + plugin.getSettingsManager().getClanChatPlayerBracketRight() + " " + plugin.getSettingsManager().getClanChatMessageColor() + msg;
        SimpleClans.log(Level.INFO, plugin.getSettingsManager().getClanChatTagBracketLeft() + clan.getTag() + plugin.getSettingsManager().getClanChatTagBracketRight() + " " + plugin.getSettingsManager().getClanChatPlayerBracketLeft() + player.getName() + plugin.getSettingsManager().getClanChatPlayerBracketRight() + " " + msg);

        List<ClanPlayer> cps = clan.getMembers();

        for (ClanPlayer cp : cps)
        {
            Player member = plugin.getServer().getPlayer(cp.getName());
            ChatBlock.sendMessage(member, message);
        }
    }

    /**
     *
     * @param event
     */
    @Override
    public void onPlayerChat(PlayerChatEvent event)
    {
        if (event.isCancelled())
        {
            return;
        }

        plugin.getClanManager().updateDisplayName(event.getPlayer());
    }

    /**
     *
     * @param event
     */
    @Override
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                plugin.getClanManager().updateLastSeen(player);
                plugin.getClanManager().updateDisplayName(player);
                plugin.getSpoutPluginManager().processPlayer(player.getName());

                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp != null)
                {
                    cp.getClan().displayBb(player);
                }
            }
        }, 1);
    }

    /**
     *
     * @param event
     */
    @Override
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        plugin.getClanManager().updateLastSeen(event.getPlayer());
        plugin.getRequestManager().endPendingRequest(event.getPlayer().getName());
    }

    /**
     *
     * @param event
     */
    @Override
    public void onPlayerKick(PlayerKickEvent event)
    {
        plugin.getClanManager().updateLastSeen(event.getPlayer());
    }

    /**
     *
     * @param event
     */
    @Override
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        if (event.isCancelled())
        {
            return;
        }

        plugin.getSpoutPluginManager().processPlayer(event.getPlayer());
    }
}
