package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.api.events.SimpleClansChunkClaimEvent;
import net.sacredlabyrinth.phaed.simpleclans.results.BankResult;
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
        if (plugin.getPermissionsManager().has(player, "simpleclans.claim")) {
            if (arg.length == 0) {

                if (cp != null) {

                    Clan clan = cp.getClan();
                    if (clan.isLeader(player)) {

                        Location loc = player.getLocation();

                        if (clan.canClaim()) {
                            ChunkLocation chunk = new ChunkLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockZ(), true);
                            Clan clanhere = plugin.getClanManager().getClanAt(loc);
                            switch (clan.canClaim(chunk)) {
                                case SUCCESS:

                                    BankResult result = clan.withdraw(50, player);

                                    switch (result) {
                                        case BANK_NOT_ENOUGH_MONEY:
                                            player.sendMessage(ChatColor.AQUA + plugin.getLang("clan.bank.not.enough.money"));
                                            break;
                                        case SUCCESS_WITHDRAW:
                                            int allowed = clan.getAllowedClaims() - clan.getClaimCount();

                                            if (clan.getClaimCount() == 0) {
                                                clan.setHomeChunk(chunk);
                                                plugin.getStorageManager().updateClan(clan);
                                            }
                                            plugin.getServer().getPluginManager().callEvent(new SimpleClansChunkClaimEvent(cp, clan, chunk));
                                            clan.addClaimedChunk(chunk);
                                            player.sendMessage(ChatColor.GRAY + MessageFormat.format(plugin.getLang("you.claimed"), allowed));
                                            break;
                                        case FAILED:
                                            player.sendMessage(ChatColor.DARK_RED + plugin.getLang("transaction.failed"));
                                            break;
                                    }
                                    break;
                                case ALREADY_OTHER:
                                    if (clan.isWarring(clanhere)) {
                                        if (plugin.getSettingsManager().isPowerBased()) {
                                            if (clanhere.getPower() * plugin.getSettingsManager().getClaimsPerPower() < clanhere.getAllowedClaims()) {
                                                if (!clanhere.getHomeChunk().equals(chunk)) {
                                                    clan.addClaimedChunk(chunk);
                                                    clanhere.removeClaimedChunk(chunk);
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
                                    break;
                                case ALREADY_OWN:
                                    player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("already.claimed"));
                                    break;
                                case NO_CLAIM_NEAR:
                                    player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("no.claim.near"));
                                    break;
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

            } else if (arg.length == 1) {
                if (arg[0].equalsIgnoreCase("info")) {

                    Location loc = player.getLocation();
                    if (cp != null) {
                        Clan clan = cp.getClan();
                        player.sendMessage("Allowed Claims: " + (clan.getAllowedClaims() - clan.getClaimCount()));
                    }
                    for (Clan clans : plugin.getClanManager().getClans()) {
                        if (clans.isClaimed(loc)) {

                            player.sendMessage("Here is a plot from " + clans.getName());
                            return;
                        }
                    }

                    player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("error.no.claim"));

                    //only temp
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
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
