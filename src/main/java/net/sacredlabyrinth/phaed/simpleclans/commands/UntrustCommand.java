package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.UUID;

/**
 * @author phaed
 */
public class UntrustCommand {
    public UntrustCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.settrust")) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
            return;
        }

        Clan clan = cp.getClan();

        if (!clan.isLeader(player)) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
            return;
        }
        if (arg.length != 1) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.0.untrust.player"), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        UUID trustedId = UUIDMigration.getForcedPlayerUUID(arg[0]);
        if (trustedId == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.matched"));
            return;
        }
        if (trustedId.equals(player.getUniqueId())) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("you.cannot.untrust.yourself"));
            return;
        }
        if (!clan.isMember(trustedId)) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("the.player.is.not.a.member.of.your.clan"));
            return;
        }
        if (clan.isLeader(trustedId)) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("leaders.cannot.be.untrusted"));
            return;
        }

        ClanPlayer tcp = plugin.getClanManager().getClanPlayer(trustedId);

        if (tcp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.matched"));
            return;
        }
        if (!tcp.isTrusted()) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("this.player.is.already.untrusted"));
            return;
        }

        clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.been.given.untrusted.status.by"), arg[0], player.getName()));
        tcp.setTrusted(false);
        plugin.getStorageManager().updateClanPlayer(tcp);
    }
}







