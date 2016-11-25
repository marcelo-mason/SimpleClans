package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author phaed
 */
public class AllyCommand {
    public AllyCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.ally")) {
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

        if (arg.length != 2) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.ally"), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        if (clan.getSize() < plugin.getSettingsManager().getClanMinSizeToAlly()) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("minimum.to.make.alliance"), plugin.getSettingsManager().getClanMinSizeToAlly()));
            return;
        }

        String action = arg[0];
        Clan ally = plugin.getClanManager().getClan(arg[1]);

        if (ally == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.clan.matched"));
            return;
        }

        if (!ally.isVerified()) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("cannot.ally.with.an.unverified.clan"));
        }

        if (action.equals(plugin.getLang("add"))) {
            if (clan.isAlly(ally.getTag())) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("your.clans.are.already.allies"));
                return;
            }

            List<ClanPlayer> onlineLeaders = Helper.stripOffLinePlayers(clan.getLeaders());

            if (onlineLeaders.isEmpty()) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("at.least.one.leader.accept.the.alliance"));
                return;
            }

            plugin.getRequestManager().addAllyRequest(cp, ally, clan);
            ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang("leaders.have.been.asked.for.an.alliance"), Helper.capitalize(ally.getName())));
        } else if (action.equals(plugin.getLang("remove"))) {
            if (!clan.isAlly(ally.getTag())) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("your.clans.are.not.allies"));
                return;
            }

            clan.removeAlly(ally);
            ally.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.broken.the.alliance"), Helper.capitalize(clan.getName()), ally.getName()));
            clan.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.broken.the.alliance"), Helper.capitalize(cp.getName()), Helper.capitalize(ally.getName())));
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.ally"), plugin.getSettingsManager().getCommandClan()));
        }
    }
}