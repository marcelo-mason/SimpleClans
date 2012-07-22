package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class ClaimCommand extends GenericCommand
{

    private SimpleClans plugin;

    public ClaimCommand(SimpleClans plugin)
    {
        super("Claiming");
        this.plugin = plugin;
        setArgumentRange(0, 1);
        setUsage(String.format(plugin.getLang("usage.claim"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("uclaim.command"));
    }

    /**
     * Execute the command
     *
     * @param player
     * @param args
     */
    public void execute(Player player, String[] args)
    {
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args)
    {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (args.length == 0) {
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
                                                player.sendMessage(ChatColor.GRAY + MessageFormat.format(plugin.getLang("you.claimed"), allowed - clan.getClaimCount()));
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
                                            if (plugin.getSettingsManager().isOnlyStealOthersOnline() && clanhere.getOnlineMembers().isEmpty()) {
                                                player.sendMessage("In the opponent clan in nobody online!");
                                                return;
                                            }
                                            if (clanhere.getClaimCount() > clanhere.getAllowedClaims()) {
                                                if (clanhere.removeClaimedChunk(chunk)) {
                                                    clan.addClaimedChunk(chunk);
                                                    clan.setHomeChunk(chunk);
                                                    player.sendMessage("you got the chunk from " + clanhere.getName());
                                                } else {
                                                    player.sendMessage("home chunk");
                                                }
                                            } else {
                                                player.sendMessage("other clan is strong enough");
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
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("info")) {
                    if (plugin.getPermissionsManager().has(player, "simpleclans.claim.info")) {

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
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                    }
                    //only temp
                } else if (args[0].equalsIgnoreCase("waypoint")) {

                    if (plugin.getPermissionsManager().has(player, "simpleclans.claim.waypoint")) {
                        Clan clan = cp.getClan();
                        ChunkLocation chunk = clan.getHomeChunk();
                        int x = chunk.getNormalX();
                        int z = chunk.getNormalZ();
                        cp.toSpoutPlayer().addWaypoint("Homeblock", x, chunk.getNormalWorld().getHighestBlockYAt(x, z), z);
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                    }
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
                player.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("usage.claim"), plugin.getSettingsManager().getCommandClan()));
            }
        }
    }
}
