package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;

/**
 * @author phaed
 */
public class KickCommand {
    public KickCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.kick")) {
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
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.kick.player"), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        String kicked = arg[0];

        if (kicked == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.matched"));
            return;
        }
        if (kicked.equalsIgnoreCase(player.getName())) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("you.cannot.kick.yourself"));
            return;
        }
        if (!clan.isMember(kicked)) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("the.player.is.not.a.member.of.your.clan"));
            return;
        }
        if (clan.isLeader(kicked)) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("you.cannot.kick.another.leader"));
            return;
        }

        clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.been.kicked.by"), Helper.capitalize(kicked), player.getName()));

        if (SimpleClans.getInstance().hasUUID()) {
            clan.removePlayerFromClan(UUIDMigration.getForcedPlayerUUID(kicked));
        } else {
            clan.removePlayerFromClan(kicked);
        }
    }
}
