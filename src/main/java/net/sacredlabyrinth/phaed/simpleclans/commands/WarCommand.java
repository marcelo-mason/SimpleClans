package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author phaed
 */
public class WarCommand {
    public WarCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.war")) {
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
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.war"), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        String action = arg[0];
        Clan war = plugin.getClanManager().getClan(arg[1]);

        if (war == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.clan.matched"));
            return;
        }
        if (!clan.isRival(war.getTag())) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("you.can.only.start.war.with.rivals"));
            return;
        }

        if (action.equals(plugin.getLang("start"))) {
            if (!clan.isWarring(war.getTag())) {
                List<ClanPlayer> onlineLeaders = Helper.stripOffLinePlayers(clan.getLeaders());

                if (!onlineLeaders.isEmpty()) {
                    plugin.getRequestManager().addWarStartRequest(cp, war, clan);
                    ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang("leaders.have.been.asked.to.accept.the.war.request"), Helper.capitalize(war.getName())));
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("at.least.one.leader.accept.the.alliance"));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clans.already.at.war"));
            }
            return;
        }

        if (action.equals(plugin.getLang("end"))) {
            if (clan.isWarring(war.getTag())) {
                plugin.getRequestManager().addWarEndRequest(cp, war, clan);
                ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang("leaders.asked.to.end.rivalry"), Helper.capitalize(war.getName())));
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clans.not.at.war"));
            }
            return;
        }

        ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.war"), plugin.getSettingsManager().getCommandClan()));
    }
}

