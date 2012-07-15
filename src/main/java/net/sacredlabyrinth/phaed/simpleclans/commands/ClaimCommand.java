package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class ClaimCommand
{

    public ClaimCommand()
    {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg)
    {
        SimpleClans plugin = SimpleClans.getInstance();

        if (arg.length == 0) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.claim.add")) {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
                Clan clan = cp.getClan();
                Location loc = player.getLocation();

                if (clan != null) {
                    if (!plugin.getClanManager().isClaimed(loc.getWorld(), loc.getBlockX(), loc.getBlockZ())) {
                        if (clan.isClaimedNear(loc.getWorld(), loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
                            clan.addClaimedLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockZ());
                            player.sendMessage("You clamed: " + (loc.getBlockX() >> 4) + "," + (loc.getBlockZ() >> 4));
                            clan.setClaimedChanged(true);
                        } else {
                            player.sendMessage("not claimed near");
                        }
                    } else {
                        player.sendMessage("Here is already a plot");
                    }
                } else {
                    player.sendMessage("No clan!");
                }
            } else {
                //no permission
            }
        } else if (arg.length == 1) {
            if (arg[0].equalsIgnoreCase("remove")) {
                if (plugin.getPermissionsManager().has(player, "simpleclans.claim.remove")) {
                    ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
                    Clan clan = cp.getClan();
                    Location loc = player.getLocation();

                    if (clan != null) {
                        if (clan.isClaimed(loc.getWorld(), loc.getBlockX(), loc.getBlockZ())) {
                            clan.removeClaimedLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockZ());
                            player.sendMessage("You removed: " + (loc.getBlockX() >> 4) + "," + (loc.getBlockZ() >> 4));
                            clan.setClaimedChanged(true);
                        } else {
                            player.sendMessage("Here is no plot");
                        }
                    } else {
                        player.sendMessage("No clan!");
                    }
                } else {
                    //no permission
                }
            } else if (arg[0].equalsIgnoreCase("info")) {
                if (plugin.getPermissionsManager().has(player, "simpleclans.claim.info")) {
                    Location loc = player.getLocation();

                    for (Clan clans : plugin.getClanManager().getClans()) {
                        if (clans.isClaimed(loc)) {
                            player.sendMessage("Here is a plot from " + clans.getName());

                        }
                    }
                } else {
                    //no permission
                }
            }
        }
    }
}
