package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.api.events.SimpleClansChunkClaimEvent;
import net.sacredlabyrinth.phaed.simpleclans.results.BankResult;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class ClaimCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public ClaimCommand(SimpleClans plugin)
    {
        super("Claim");
        this.plugin = plugin;
        setArgumentRange(0, 1);
        setUsages(MessageFormat.format(plugin.getLang("usage.claim"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("claim.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp != null) {
            if (cp.isLeader() && plugin.getPermissionsManager().has(sender, "simpleclans.claim")) {
                return MessageFormat.format(plugin.getLang("usage.menu.claim"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
            }
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
        if (plugin.getPermissionsManager().has(player, "simpleclans.claim")) {
            if (args.length == 0) {

                if (cp != null) {

                    Clan clan = cp.getClan();
                    if (clan.isLeader(player)) {

                        Location loc = player.getLocation();

                        if (clan.canClaim()) {
                            ChunkLocation chunk = new ChunkLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockZ(), true);
                            Clan clanhere = plugin.getClanManager().getClanAt(loc);
                            switch (clan.canClaim(chunk)) {
                                case SUCCESS:

                                    BankResult result = clan.withdraw(plugin.getSettingsManager().getClaimPrize(), player);

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
                                            player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("already.claimed"));
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

            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("info")) {

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
                } else if (args[0].equalsIgnoreCase("sethomeblock") || args[0].equalsIgnoreCase("sethb")) {
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
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            }
        }
    }
}
