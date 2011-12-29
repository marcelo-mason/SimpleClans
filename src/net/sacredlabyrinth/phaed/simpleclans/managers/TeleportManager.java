package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.TeleportState;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.HashMap;

public final class TeleportManager
{
    private SimpleClans plugin;
    private HashMap<String, TeleportState> waitingPlayers = new HashMap<String, TeleportState>();

    /**
     *
     */
    public TeleportManager()
    {
        plugin = SimpleClans.getInstance();
        startCounter();
    }

    public void addPlayer(Player player, Location dest, String clanName)
    {
        int secs = SimpleClans.getInstance().getSettingsManager().getWaitSecs();

        if (!waitingPlayers.containsKey(player.getName()))
        {
            waitingPlayers.put(player.getName(), new TeleportState(player, dest, clanName));

            if (secs > 0)
            {
                ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang().getString("waiting.for.teleport.stand.still.for.0.seconds"), secs));
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("already.waiting.for.teleport"));
        }
    }

    public void startCounter()
    {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
        {
            public void run()
            {
                for (TeleportState state : waitingPlayers.values())
                {
                    Player player = state.getPlayer();

                    if (player != null)
                    {
                        if (state.isTeleportTime())
                        {
                            if (Helper.isSameBlock(player.getLocation(), state.getLocation()))
                            {
                                Location loc = state.getLocation();

                                int x = loc.getBlockX();
                                int z = loc.getBlockZ();

                                player.sendBlockChange(new Location(loc.getWorld(), x + 1, loc.getBlockY() - 1, z + 1), Material.GLASS, (byte) 0);
                                player.sendBlockChange(new Location(loc.getWorld(), x - 1, loc.getBlockY() - 1, z - 1), Material.GLASS, (byte) 0);
                                player.sendBlockChange(new Location(loc.getWorld(), x + 1, loc.getBlockY() - 1, z - 1), Material.GLASS, (byte) 0);
                                player.sendBlockChange(new Location(loc.getWorld(), x - 1, loc.getBlockY() - 1, z + 1), Material.GLASS, (byte) 0);

                                player.teleport(new Location(loc.getWorld(), loc.getBlockX() + .5, loc.getBlockY(), loc.getBlockZ() + .5));

                                ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang().getString("now.at.homebase"), state.getClanName()));
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("you.moved.teleport.cancelled"));
                            }

                            waitingPlayers.remove(player.getName());
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + "" + state.getCounter());
                        }
                    }
                }
            }
        }, 0, 20L);
    }
}