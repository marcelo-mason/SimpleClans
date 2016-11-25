package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * @author phaed
 */
public class InviteCommand {
    public InviteCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.invite")) {
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
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.0.invite.player"), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        Player invited = Helper.getPlayer(arg[0]);

        if (invited == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.matched"));
            return;
        }
        if (!plugin.getPermissionsManager().has(invited, "simpleclans.member.can-join")) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("the.player.doesn.t.not.have.the.permissions.to.join.clans"));
            return;
        }
        if (invited.getName().equals(player.getName())) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("you.cannot.invite.yourself"));
            return;
        }
        if (plugin.getSettingsManager().isBanned(player.getName())) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("this.player.is.banned.from.using.clan.commands"));
            return;
        }

        ClanPlayer cpInv = plugin.getClanManager().getClanPlayer(invited);

        if (cpInv != null) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("the.player.is.already.member.of.another.clan"));
            return;
        }
        if (!plugin.getClanManager().purchaseInvite(player)) {
            return;
        }
        if (clan.getSize() >= plugin.getSettingsManager().getMaxMembers()) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("the.clan.members.reached.limit"));
            return;
        }

        plugin.getRequestManager().addInviteRequest(cp, invited.getName(), clan);
        ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.been.asked.to.join"), Helper.capitalize(invited.getName()), clan.getName()));
    }
}
