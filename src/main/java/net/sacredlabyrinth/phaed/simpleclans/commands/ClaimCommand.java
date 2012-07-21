package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
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

        if (arg.length == 0) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.claim.add")) {
                if (cp != null) {

                    Clan clan = cp.getClan();
                    if (clan.isLeader(player)) {

                        Location loc = player.getLocation();

                        if (clan.canClaim()) {
                            ChunkLocation chunk = new ChunkLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockZ(), true);
                            Clan clanhere = plugin.getClanManager().getClanAt(loc);
                            if (clanhere == null) {
                                if (clan.isClaimedNear(loc.getWorld(), loc.getBlockX(), loc.getBlockZ())) {
                                    BankResult result = clan.withdraw(50, player);

                                    switch (result) {
                                        case BANK_NOT_ENOUGH_MONEY:
                                            player.sendMessage(ChatColor.AQUA + plugin.getLang("clan.bank.not.enough.money"));
                                            break;
                                        case SUCCESS_WITHDRAW:
                                            int allowed = clan.getAllowedClaims();

                                            if (clan.getClaimCount() == 0) {
                                                clan.setHomeChunk(chunk);
                                                plugin.getStorageManager().updateClan(clan);
                                            }

                                            clan.addClaimedChunk(chunk);
                                            player.sendMessage(ChatColor.GRAY + MessageFormat.format(plugin.getLang("you.claimed"), allowed));
                                            break;
                                        case FAILED:
                                            player.sendMessage(ChatColor.DARK_RED + plugin.getLang("transaction.failed"));
                                            break;
                                    }

                                } else {
                                    player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("no.claim.near"));
                                }
                            } else {
                                if (clan.isWarring(clanhere)) {
                                    if (plugin.getSettingsManager().isPowerBased()) {
                                        if (clanhere.getPower() * plugin.getSettingsManager().getClaimsPerPower() < clanhere.getAllowedClaims()) {
                                            if (clanhere.removeClaimedChunk(chunk)) {
                                                clan.addClaimedChunk(chunk);
                                                player.sendMessage("you got the chunk from " + clanhere.getName());
                                            } else {
                                                player.sendMessage("home chunk");
                                            }
                                        }
                                    } else {
                                        throw new UnsupportedOperationException("not yet");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("already.claimed"));
                                }
                            }
                        } else {
                            player.sendMessage(ChatColor.DARK_RED + plugin.getLang("no.claims.left"));
                        }
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
                    }
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            }
        } else if (arg.length == 1) {
            if (arg[0].equalsIgnoreCase("info")) {
                if (plugin.getPermissionsManager().has(player, "simpleclans.claim.info")) {

                    Location loc = player.getLocation();
                    if (cp != null) {
                        Clan clan = cp.getClan();
                        player.sendMessage("Allowed Claims: " + clan.getAllowedClaims());
                    }
                    for (Clan clans : plugin.getClanManager().getClans()) {
                        if (clans.isClaimed(loc)) {

                            player.sendMessage("Here is a plot from " + clans.getName());
                            return;
                        }
                    }

                    player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("error.no.claim"));
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                }
                //only temp
            } else if (arg[0].equalsIgnoreCase("waypoint")) {

                if (plugin.getPermissionsManager().has(player, "simpleclans.claim.waypoint")) {
                    Clan clan = cp.getClan();
                    ChunkLocation chunk = clan.getHomeChunk();
                    int x = chunk.getNormalX();
                    int z = chunk.getNormalZ();
                    cp.toSpoutPlayer().addWaypoint("Homeblock", x, chunk.getNormalWorld().getHighestBlockYAt(x, z), z);
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                }
            } else if (arg[0].equalsIgnoreCase("sethomeblock") || arg[0].equalsIgnoreCase("sethb")) {
                if (plugin.getPermissionsManager().has(player, "simpleclans.claim.sethomeblock")) {

                    if (cp != null) {
                        Clan clan = cp.getClan();
                        Location loc = player.getLocation();
                        ChunkLocation chunk = new ChunkLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockZ(), true);

                        if (clan.isClaimed(chunk)) {
                            if (!chunk.equals(clan.getHomeChunk())) {
                                clan.setHomeChunk(new ChunkLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockZ(), true));
                                plugin.getStorageManager().updateClan(clan);
                                player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("homeblock.moved"));
                            } else {
                                player.sendMessage(ChatColor.DARK_RED + plugin.getLang("already.homeblock"));
                            }
                        } else {
                            player.sendMessage(ChatColor.DARK_RED + plugin.getLang("error.no.claim"));
                        }
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
                    }

                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                }
            } else {
                player.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("usage.claim"), plugin.getSettingsManager().getCommandClan()));
            }
        } else {
            player.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("usage.claim"), plugin.getSettingsManager().getCommandClan()));
        }
    }
}
