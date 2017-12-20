package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class ResetKDRCommand {

    public ResetKDRCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        if (arg == null || arg.length == 0) {
            if (!plugin.getSettingsManager().isAllowResetKdr()) {
                ChatBlock.sendMessage(player, ChatColor.RED
                        + MessageFormat.format(plugin.getLang("usage.resetkdr"),
                                plugin.getSettingsManager().getCommandClan()));
                return;
            }
            if (!plugin.getPermissionsManager().has(player, "simpleclans.member.resetkdr")) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                return;
            }
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
            if (cp == null) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
                return;
            }
            if (plugin.getClanManager().purchaseResetKdr(player)) {
                plugin.getClanManager().resetKdr(cp);
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("you.have.reseted.your.kdr"));
            }
            return;
        }
        if (arg.length == 1 && plugin.getPermissionsManager().has(player, "simpleclans.admin.resetkdr")) {
            if (arg[0].equalsIgnoreCase("all")) {
                for (ClanPlayer cp : SimpleClans.getInstance().getClanManager().getAllClanPlayers()) {
                    plugin.getClanManager().resetKdr(cp);
                }
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("you.have.reseted.kdr.of.all.players"));
            } else {
                Player toReset = Helper.getPlayer(arg[0]);
                if (toReset == null) {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.matched"));
                    return;
                }
                ClanPlayer trcp = plugin.getClanManager().getClanPlayer(toReset);
                if (trcp == null) {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.data.found"));
                    return;
                }
                plugin.getClanManager().resetKdr(trcp);
                ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("you.have.reseted.0.kdr"), toReset.getName()));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
