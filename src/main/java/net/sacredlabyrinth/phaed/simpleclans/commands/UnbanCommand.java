package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;

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

        UUID uuid = UUIDMigration.getForcedPlayerUUID(arg[0]);
        if (uuid == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.matched"));
            return;
        }

        if (!plugin.getSettingsManager().isBanned(uuid)) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("this.player.is.not.banned"));
            return;
        }

        Player pl = Bukkit.getPlayer(uuid);
        if (pl != null) {
            ChatBlock.sendMessage(pl, ChatColor.AQUA + plugin.getLang("you.have.been.unbanned.from.clan.commands"));
        }

        plugin.getSettingsManager().removeBanned(uuid);
        ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("player.removed.from.the.banned.list"));
    }
}



