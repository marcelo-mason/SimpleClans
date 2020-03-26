package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.UUID;

public class SetRankCommand {
    public SetRankCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.setrank")) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
            return;
        }

        Clan clan = cp.getClan();

        if (!clan.isVerified()) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.is.not.verified"));
            return;
        }
        if (!clan.isLeader(player)) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
            return;
        }
        if (arg.length < 1) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.setrank"), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        UUID uuid = UUIDMigration.getForcedPlayerUUID(arg[0]);        
        if (uuid == null || (!clan.isMember(uuid) && !clan.isLeader(uuid))) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.matched"));
            return;
        }
        
        String rank = Helper.toMessage(Helper.removeFirst(arg));
        if (rank.contains("&") && !plugin.getPermissionsManager().has(player, "simpleclans.leader.coloredrank")) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("you.cannot.set.colored.ranks"));
        	return;
        }

        ClanPlayer cpm = plugin.getClanManager().getClanPlayer(uuid);
        cpm.setRank(rank);
        plugin.getStorageManager().updateClanPlayer(cpm);

        ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("player.rank.changed"));
    }
}





