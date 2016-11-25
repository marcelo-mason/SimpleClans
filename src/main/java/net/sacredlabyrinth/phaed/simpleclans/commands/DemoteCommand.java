package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.UUID;

import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;

/**
 * @author phaed
 */
public class DemoteCommand {
    public DemoteCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.demote")) {
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
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.demote.leader"), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        String demotedName = arg[0];
        boolean allOtherLeadersOnline;

        if (demotedName == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.matched"));
            return;
        }

        if (SimpleClans.getInstance().hasUUID()) {
            UUID PlayerUniqueId = UUIDMigration.getForcedPlayerUUID(demotedName);
            if (PlayerUniqueId == null) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.matched"));
                return;
            }
            allOtherLeadersOnline = clan.allOtherLeadersOnline(PlayerUniqueId);
        } else {
            allOtherLeadersOnline = clan.allOtherLeadersOnline(demotedName);
        }

        if (!allOtherLeadersOnline) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("leaders.must.be.online.to.vote.on.demotion"));
            return;
        }

        if (SimpleClans.getInstance().hasUUID()) {
            UUID PlayerUniqueId = UUIDMigration.getForcedPlayerUUID(demotedName);
            if (!clan.isLeader(PlayerUniqueId)) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("player.is.not.a.leader.of.your.clan"));
                return;
            }
            if (clan.getLeaders().size() != 1 && plugin.getSettingsManager().isConfirmationForDemote()) {
                plugin.getRequestManager().addDemoteRequest(cp, demotedName, clan);
                ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("demotion.vote.has.been.requested.from.all.leaders"));
                return;
            }
            clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("demoted.back.to.member"), Helper.capitalize(demotedName)));
            clan.demote(PlayerUniqueId);
        } else {
            if (!clan.isLeader(demotedName)) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("player.is.not.a.leader.of.your.clan"));
                return;
            }
            if (clan.getLeaders().size() != 1 && plugin.getSettingsManager().isConfirmationForDemote()) {
                plugin.getRequestManager().addDemoteRequest(cp, demotedName, clan);
                ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("demotion.vote.has.been.requested.from.all.leaders"));
                return;
            }
            clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("demoted.back.to.member"), Helper.capitalize(demotedName)));
            clan.demote(demotedName);
        }
    }
}
