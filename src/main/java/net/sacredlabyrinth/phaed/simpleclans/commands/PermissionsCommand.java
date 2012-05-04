package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author p000ison
 */
public class PermissionsCommand {

    public PermissionsCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (plugin.getPermissionsManager().has(player, "simpleclans.admin.permissions")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                if (arg.length == 3) {
                    if (plugin.getClanManager().getClan(arg[1]) != null) {
                        Clan clan = plugin.getClanManager().getClan(arg[1]);
                        if (arg[0].equalsIgnoreCase("add")) {
                            SimpleClans.getInstance().getClanManager().addPermission(clan.getName(), arg[2]);
                            SimpleClans.getInstance().getClanManager().updateAllPermissions(clan);
                            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("permissions.added.node"), arg[2], arg[1]));
                        } else if (arg[0].equalsIgnoreCase("remove")) {
                            SimpleClans.getInstance().getClanManager().removePermission(clan.getName(), arg[2]);
                            SimpleClans.getInstance().getClanManager().updateAllPermissions(clan);
                            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("permissions.removed.node"), arg[2], arg[1]));
                        }
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("the.clan.does.not.exist"));
                    }
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.0.permissions.player"), plugin.getSettingsManager().getCommandClan()));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}