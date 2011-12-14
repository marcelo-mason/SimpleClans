package net.sacredlabyrinth.phaed.simpleclans.listeners;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;

import java.util.Iterator;

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

            event.setCancelled(true);

            if (split.length > 1)
            {
                plugin.getClanManager().processClanChat(player, command, Helper.toMessage(Helper.removeFirst(split)));
            }
        }
        else if (command.equalsIgnoreCase(plugin.getSettingsManager().getCommandAlly()))
        {
            if (!plugin.getSettingsManager().isAllyChatEnable())
            {
                return;
            }

            event.setCancelled(true);

            if (split.length > 1)
            {
                plugin.getClanManager().processAllyChat(player, Helper.toMessage(Helper.removeFirst(split)));
            }
        }
        else if (command.equalsIgnoreCase(plugin.getSettingsManager().getCommandGlobal()))
        {
            event.setCancelled(true);

            if (split.length > 1)
            {
                plugin.getClanManager().processGlobalChat(player, Helper.toMessage(Helper.removeFirst(split)));
            }
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

        if (event.getPlayer() == null)
        {
            return;
        }

        String message = event.getMessage();
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(event.getPlayer());

        if (cp != null)
        {
            if (cp.getChannel().equals(ClanPlayer.Channel.CLAN))
            {
                plugin.getClanManager().processClanChat(event.getPlayer(), message);
                event.setCancelled(true);
            }
            else if (cp.getChannel().equals(ClanPlayer.Channel.ALLY))
            {
                plugin.getClanManager().processAllyChat(event.getPlayer(), message);
                event.setCancelled(true);
            }
        }

        if (!plugin.getPermissionsManager().has(event.getPlayer(), "simpleclans.mod.nohide"))
        {
            boolean isClanChat = event.getMessage().contains("" + ChatColor.RED + ChatColor.WHITE + ChatColor.RED + ChatColor.BLACK);
            boolean isAllyChat = event.getMessage().contains("" + ChatColor.AQUA + ChatColor.WHITE + ChatColor.AQUA + ChatColor.BLACK);

            for (Iterator iter = event.getRecipients().iterator(); iter.hasNext(); )
            {
                Player player = (Player) iter.next();

                ClanPlayer rcp = plugin.getClanManager().getClanPlayer(player);

                if (rcp != null)
                {
                    if (!rcp.isClanChat())
                    {
                        if (isClanChat)
                        {
                            iter.remove();
                            continue;
                        }
                    }

                    if (!rcp.isAllyChat())
                    {
                        if (isAllyChat)
                        {
                            iter.remove();
                            continue;
                        }
                    }

                    if (!rcp.isGlobalChat())
                    {
                        if (!isAllyChat && !isClanChat)
                        {
                            iter.remove();
                            continue;
                        }
                    }
                }
            }
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
                        if (cp.isBbEnabled())
                        {
                            cp.getClan().displayBb(player);
                        }
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
