package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author roinujnosde
 */
public class PurgeCommand {

    /**
     * Executes the command
     *
     * @param sender
     * @param args
     */
    public void execute(CommandSender sender, String args[]) {
        SimpleClans plugin = SimpleClans.getInstance();
        if (sender instanceof Player) {
            if (!plugin.getPermissionsManager().has((Player) sender, "simpleclans.admin.purge")) {
                ChatBlock.sendMessage(sender, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                return;
            }
        }

        if (args.length == 1) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(args[0]);
            if (cp == null) {
                ChatBlock.sendMessage(sender, ChatColor.RED + plugin.getLang("no.player.matched"));
                return;
            }

            plugin.getClanManager().deleteClanPlayer(cp);
            ChatBlock.sendMessage(sender, ChatColor.AQUA + plugin.getLang("player.purged"));
            return;
        }

        ChatBlock.sendMessage(sender, ChatColor.RED
                + MessageFormat.format(plugin.getLang("usage.purge"), plugin.getSettingsManager().getCommandClan()));
    }
}
