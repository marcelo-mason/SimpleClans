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
public class UnClaimCommand
{

    public UnClaimCommand()
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

            if (arg.length == 0) {
                if (plugin.getPermissionsManager().has(player, "simpleclans.claim.unclaim")) {

                    if (clan.isLeader(player)) {
                        Location loc = player.getLocation();
                        ChunkLocation chunk = new ChunkLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockZ(), true);

                        if (clan.isClaimed(chunk)) {
                            if (clan.isClaimedNear(chunk, chunk)) {
                                if (clan.removeClaimedChunk(chunk)) {
                                    player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("claim.removed"));
                                } else {
                                    player.sendMessage(ChatColor.DARK_RED + plugin.getLang("remove.homeblock"));
                                }
                            } else {
                                player.sendMessage("no remove");
                            }
                        } else {
                            player.sendMessage(ChatColor.DARK_RED + plugin.getLang("error.no.claim"));
                        }
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                    }
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                }
            } else {
                player.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("usage.unclaim"), plugin.getSettingsManager().getCommandClan()));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
        }
    }
}
