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

		UUID uuid = UUIDMigration.getForcedPlayerUUID(arg[0]);
		if (uuid == null) {
			ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.matched"));
			return;
		}
		
		if (plugin.getSettingsManager().isBanned(uuid)) {
			ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("this.player.is.already.banned"));
			return;
		}

		plugin.getClanManager().ban(uuid);
		ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("player.added.to.banned.list"));

		Player pl = SimpleClans.getInstance().getServer().getPlayer(uuid);
		if (pl != null) {
			ChatBlock.sendMessage(pl, ChatColor.AQUA + plugin.getLang("you.banned"));
		}
    }
}
