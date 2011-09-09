package net.sacredlabyrinth.phaed.simpleclans.listeners;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;

/**
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

        if (plugin.getSettingsManager().isBlacklistedWorld(player.getLocation().getWorld().getName()))
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

        if (plugin.getClanManager().isClan(command))
        {
            if (!plugin.getSettingsManager().getClanChatEnable())
            {
                return;
            }

            plugin.getCommandManager().processClanChat(player, command, Helper.toMessage(Helper.removeFirst(split)));
            event.setCancelled(true);
        }
        else if (command.equalsIgnoreCase(plugin.getSettingsManager().getCommandClan()))
        {
            plugin.getCommandManager().processClan(player, Helper.removeFirst(split));
            event.setCancelled(true);
        }
        else if (command.equalsIgnoreCase(plugin.getSettingsManager().getCommandAccept()))
        {
            plugin.getCommandManager().processAccept(player);
            event.setCancelled(true);
        }
        else if (command.equalsIgnoreCase(plugin.getSettingsManager().getCommandDeny()))
        {
            plugin.getCommandManager().processDeny(player);
            event.setCancelled(true);
        }
        else if (command.equalsIgnoreCase(plugin.getSettingsManager().getCommandMore()))
        {
            plugin.getCommandManager().processMore(player);
            event.setCancelled(true);
        }

    }

    /**
     * @param event
     */
    @Override
    public void onPlayerChat(PlayerChatEvent event)
    {
        if (event.isCancelled())
        {
            return;
        }

        if (plugin.getSettingsManager().isBlacklistedWorld(event.getPlayer().getLocation().getWorld().getName()))
        {
            return;
        }

        plugin.getClanManager().updateDisplayName(event.getPlayer());
    }

    /**
     * @param event
     */
    @Override
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();

        if (plugin.getSettingsManager().isBlacklistedWorld(player.getLocation().getWorld().getName()))
        {
            return;
        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            public void run()
            {
                plugin.getClanManager().updateLastSeen(player);
                plugin.getClanManager().updateDisplayName(player);
                plugin.getSpoutPluginManager().processPlayer(player.getName());

                if (plugin.getSettingsManager().isBbShowOnLogin())
                {
                    ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                    if (cp != null)
                    {
                        cp.getClan().displayBb(player);
                    }
                }
            }
        }, 1);
    }

    /**
     * @param event
     */
    @Override
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        if (plugin.getSettingsManager().isBlacklistedWorld(event.getPlayer().getLocation().getWorld().getName()))
        {
            return;
        }

        plugin.getClanManager().updateLastSeen(event.getPlayer());
        plugin.getRequestManager().endPendingRequest(event.getPlayer().getName());
    }

    /**
     * @param event
     */
    @Override
    public void onPlayerKick(PlayerKickEvent event)
    {
        if (plugin.getSettingsManager().isBlacklistedWorld(event.getPlayer().getLocation().getWorld().getName()))
        {
            return;
        }

        plugin.getClanManager().updateLastSeen(event.getPlayer());
    }

    /**
     * @param event
     */
    @Override
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        if (event.isCancelled())
        {
            return;
        }

        if (plugin.getSettingsManager().isBlacklistedWorld(event.getPlayer().getLocation().getWorld().getName()))
        {
            return;
        }

        plugin.getSpoutPluginManager().processPlayer(event.getPlayer());
    }

    /**
     * @param event
     */
    @Override
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event)
    {
        plugin.getSpoutPluginManager().processPlayer(event.getPlayer());
    }
}
