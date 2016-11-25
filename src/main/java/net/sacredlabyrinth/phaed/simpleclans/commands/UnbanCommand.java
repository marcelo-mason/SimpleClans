package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * @author phaed
 */
public class UnbanCommand {
    public UnbanCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.mod.ban")) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            return;
        }
        if (arg.length != 1) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.ban.unban"), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        String banned = arg[0];

        if (!plugin.getSettingsManager().isBanned(banned)) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("this.player.is.not.banned"));
            return;
        }

        Player pl = Helper.getPlayer(banned);

        if (pl != null) {
            ChatBlock.sendMessage(pl, ChatColor.AQUA + plugin.getLang("you.have.been.unbanned.from.clan.commands"));
        }

        plugin.getSettingsManager().removeBanned(banned);
        ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("player.removed.from.the.banned.list"));
    }
}



