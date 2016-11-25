package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class BbCommand {

    public BbCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

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

        if (arg.length == 0) {
            if (!plugin.getPermissionsManager().has(player, "simpleclans.member.bb")) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                return;
            }
            clan.displayBb(player);
            return;
        }

        if (arg.length == 1 && arg[0].equalsIgnoreCase("clear")) {
            if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.bb-clear")) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                return;
            }
            if (!cp.isTrusted() || !cp.isLeader()) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                return;
            }
            cp.getClan().clearBb();
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("cleared.bb"));
            return;
        }

        if (!plugin.getPermissionsManager().has(player, "simpleclans.member.bb-add")) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            return;
        }

        if (!cp.isTrusted()) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
            return;
        }

        String msg = Helper.toMessage(arg);
        clan.addBb(player.getName(), ChatColor.AQUA + player.getName() + ": " + ChatColor.WHITE + msg);
        plugin.getStorageManager().updateClan(clan);
    }
}