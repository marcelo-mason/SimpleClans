package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
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
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp != null) {

            Clan clan = cp.getClan();

            if (clan.isLeader(player)) {

                if (arg.length == 0) {
                    if (plugin.getPermissionsManager().has(player, "simpleclans.claim.add")) {

                        Location loc = player.getLocation();

                        if (clan.canClaim()) {
                            if (!plugin.getClanManager().isClaimed(loc.getWorld(), loc.getBlockX(), loc.getBlockZ())) {
                                if (clan.isClaimedNear(loc.getWorld(), loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
                                    ChunkLocation chunk = new ChunkLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockZ(), true);

                                    if (clan.getClaimCount() == 0) {
                                        clan.setHomeChunk(chunk);
                                        plugin.getStorageManager().updateClan(clan);
                                    }

                                    clan.addClaimedChunk(chunk);

                                    player.sendMessage("You clamed: " + (loc.getBlockX() >> 4) + "," + (loc.getBlockZ() >> 4) + " " + clan.getAllowedClaims());
                                    clan.setClaimedChanged(true);
                                } else {
                                    player.sendMessage("not claimed near");
                                }
                            } else {
                                player.sendMessage("Here is already a plot");
                            }
                        } else {
                            player.sendMessage("Not enought claims! ");
                        }
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                    }
                } else if (arg.length == 1) {
                    if (arg[0].equalsIgnoreCase("remove")) {
                        if (plugin.getPermissionsManager().has(player, "simpleclans.claim.remove")) {

                            Location loc = player.getLocation();

                            if (clan.isClaimed(loc.getWorld(), loc.getBlockX(), loc.getBlockZ())) {
                                if (clan.removeClaimedLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockZ())) {
                                    player.sendMessage("You removed: " + (loc.getBlockX() >> 4) + "," + (loc.getBlockZ() >> 4));
                                    clan.setClaimedChanged(true);
                                } else {
                                    player.sendMessage("this is a home block");
                                }

                            } else {
                                player.sendMessage("Here is no plot");
                            }

                        } else {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                        }
                    } else if (arg[0].equalsIgnoreCase("info")) {
                        if (plugin.getPermissionsManager().has(player, "simpleclans.claim.info")) {

                            Location loc = player.getLocation();

                            for (Clan clans : plugin.getClanManager().getClans()) {
                                if (clans.isClaimed(loc)) {
                                    player.sendMessage("Allowed Claims: " + clans.getAllowedClaims());
                                    player.sendMessage("Here is a plot from " + clans.getName());
                                }
                            }
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                        }
                    } else if (arg[0].equalsIgnoreCase("sethomeblock")) {
                        if (plugin.getPermissionsManager().has(player, "simpleclans.claim.sethomeblock")) {

                            Location loc = player.getLocation();
                            ChunkLocation chunk = new ChunkLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockZ(), true);

                            if (clan.isClaimed(chunk)) {
                                if (!chunk.equals(clan.getHomeChunk())) {
                                    clan.setHomeChunk(new ChunkLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockZ(), true));
                                    plugin.getStorageManager().updateClan(clan);
                                } else {
                                    player.sendMessage("Here is already a home block");
                                }
                            } else {
                                player.sendMessage("Here is no plot");
                            }

                        } else {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                        }
                    }
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
        }
    }
}
