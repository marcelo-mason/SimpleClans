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
public class ResignCommand {
    public ResignCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.member.resign")) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            return;
        }
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
            return;
        }
        Clan clan = cp.getClan();

        if (!clan.isLeader(player) || clan.getLeaders().size() > 1) {
            clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("0.has.resigned"), Helper.capitalize(player.getName())));
            if (SimpleClans.getInstance().hasUUID()) {
                clan.removePlayerFromClan(player.getUniqueId());
            } else {
                clan.removePlayerFromClan(player.getName());
            }
        } else if (clan.isLeader(player) && clan.getLeaders().size() == 1) {
            plugin.getClanManager().serverAnnounce(ChatColor.AQUA + MessageFormat.format(plugin.getLang("clan.has.been.disbanded"), clan.getName()));
            clan.disband();
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("last.leader.cannot.resign.you.must.appoint.another.leader.or.disband.the.clan"));
        }
    }
}
