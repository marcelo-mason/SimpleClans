package net.sacredlabyrinth.phaed.simpleclans.commands;

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
        if (plugin.getPermissionsManager().has(player, "simpleclans.admin.resetkdr")) {
            for (ClanPlayer cp : SimpleClans.getInstance().getClanManager().getAllClanPlayers()) {
                cp.setCivilianKills(0);
                cp.setNeutralKills(0);
                cp.setRivalKills(0);
                cp.setDeaths(0);
            }
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("kdr.of.all.players.was.reset"));
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
