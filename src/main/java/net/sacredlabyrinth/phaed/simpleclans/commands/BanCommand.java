package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.UUID;

import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;

/**
 * @author phaed
 */
public class BanCommand {
    public BanCommand() {
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
            ChatBlock.sendMessage(player, MessageFormat.format(plugin.getLang("usage.ban.unban"), ChatColor.RED, plugin.getSettingsManager().getCommandClan()));
            return;
        }

        String banned = arg[0];

        if (SimpleClans.getInstance().hasUUID()) {
            UUID PlayerUniqueId = UUIDMigration.getForcedPlayerUUID(banned);
            if (plugin.getSettingsManager().isBanned(PlayerUniqueId)) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("this.player.is.already.banned"));
                return;
            }

            Player pl = SimpleClans.getInstance().getServer().getPlayer(PlayerUniqueId);

            if (pl != null) {
                ChatBlock.sendMessage(pl, ChatColor.AQUA + plugin.getLang("you.banned"));
            }

            plugin.getClanManager().ban(banned);
            ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("player.added.to.banned.list"));
        } else {
            if (plugin.getSettingsManager().isBanned(banned)) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("this.player.is.already.banned"));
                return;
            }

            Player pl = SimpleClans.getInstance().getServer().getPlayerExact(banned);

            if (pl != null) {
                ChatBlock.sendMessage(pl, ChatColor.AQUA + plugin.getLang("you.banned"));
            }

            plugin.getClanManager().ban(banned);
            ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("player.added.to.banned.list"));
        }

    }
}
