package net.sacredlabyrinth.phaed.simpleclans.listeners;

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
        String[] split = event.getMessage().substring(1).split(" ");

        if (split.length == 0)
        {
            return;
        }

        String command = split[0];
        String[] arg = Helper.removeFirst(split);

        if (plugin.getClanManager().isClan(command))
        {
            Clan clan = plugin.getClanManager().getClan(command);

            plugin.getCommandManager().processClanChat(event.getPlayer(), clan, Helper.toMessage(arg));
            event.setCancelled(true);
        }
        else if (command.equalsIgnoreCase(plugin.getSettingsManager().getCommandClan()))
        {
            if (arg.length == 0)
            {
                plugin.getCommandManager().processMenu(event.getPlayer());
            }
            else
            {
                plugin.getCommandManager().processClan(event.getPlayer(), arg[0], Helper.removeFirst(arg));
            }

            event.setCancelled(true);
        }
        else if (command.equalsIgnoreCase(plugin.getSettingsManager().getCommandMore()))
        {
            plugin.getCommandManager().processMore(event.getPlayer());
            event.setCancelled(true);
        }
        else if (command.equalsIgnoreCase(plugin.getSettingsManager().getCommandAccept()))
        {
            plugin.getCommandManager().processAccept(event.getPlayer());
            event.setCancelled(true);
        }
        else if (command.equalsIgnoreCase(plugin.getSettingsManager().getCommandDeny()))
        {
            plugin.getCommandManager().processDeny(event.getPlayer());
            event.setCancelled(true);
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
