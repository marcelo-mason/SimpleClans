package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class ReloadCommand
{
    public ReloadCommand()
    {
    }

    /**
     * Execute the command
     *
     * @param sender
     * @param arg
     */
    public void execute(CommandSender sender, String[] arg)
    {
        SimpleClans plugin = SimpleClans.getInstance();

        if (sender instanceof Player && !plugin.getPermissionsManager().has((Player)sender, "simpleclans.admin.reload"))
        {
        	ChatBlock.sendMessage(sender, ChatColor.RED + "Does not match a clan command");
        	return;
        }

        plugin.getSettingsManager().load();
        plugin.getLanguageManager().load();
        plugin.getStorageManager().importFromDatabase();
        SimpleClans.getInstance().getPermissionsManager().loadPermissions();

        for (Clan clan : plugin.getClanManager().getClans())
        {
            SimpleClans.getInstance().getPermissionsManager().updateClanPermissions(clan);
        }
        ChatBlock.sendMessage(sender, ChatColor.AQUA + plugin.getLang("configuration.reloaded"));

    }
}
