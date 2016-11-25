package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * @author phaed
 */
public class TrustCommand {
    public TrustCommand() {
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
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.0.trust.player"), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        String trusted = arg[0];

        if (trusted == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.matched"));
            return;
        }
        if (trusted.equals(player.getName())) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("you.cannot.trust.yourself"));
            return;
        }
        if (!clan.isMember(trusted)) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("the.player.is.not.a.member.of.your.clan"));
            return;
        }
        if (clan.isLeader(trusted)) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("leaders.are.already.trusted"));
            return;
        }

        ClanPlayer tcp = plugin.getClanManager().getClanPlayerName(trusted);

        if (tcp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.matched"));
            return;
        }

        if (tcp.isTrusted()) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("this.player.is.already.trusted"));
            return;
        }

        clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.been.given.trusted.status.by"), Helper.capitalize(trusted), player.getName()));
        tcp.setTrusted(true);
        plugin.getStorageManager().updateClanPlayer(tcp);
    }

}







