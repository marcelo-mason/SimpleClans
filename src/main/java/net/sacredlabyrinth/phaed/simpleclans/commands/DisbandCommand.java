package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * @author phaed
 */
public class DisbandCommand {
    public DisbandCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (arg.length == 0) {
            if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.disband")) {
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
            if (clan.getLeaders().size() != 1) {
                plugin.getRequestManager().addDisbandRequest(cp, clan);
                ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("clan.disband.vote.has.been.requested.from.all.leaders"));
                return;
            }

            clan.clanAnnounce(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("clan.has.been.disbanded"), clan.getName()));
            clan.disband();
            return;
        }

        if (arg.length > 1) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.0.disband"), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        if (!plugin.getPermissionsManager().has(player, "simpleclans.mod.disband")) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            return;
        }

        Clan clan = plugin.getClanManager().getClan(arg[0]);

        if (clan == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.clan.matched"));
            return;
        }

        plugin.getClanManager().serverAnnounce(ChatColor.AQUA + MessageFormat.format(plugin.getLang("clan.has.been.disbanded"), clan.getName()));
        clan.disband();
    }
}
