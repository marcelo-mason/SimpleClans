package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class ReloadCommand
{
    public ReloadCommand()
    {
    }

    /**
     * Run the command
     * @param player
     * @param arg
     */
    public void run(Player player, String[] arg)
    {
        SimpleClans plugin = SimpleClans.getInstance();

        if (plugin.getPermissionsManager().has(player, "simpleclans.admin.reload"))
        {
            plugin.getSettingsManager().load();
            plugin.getStorageManager().importFromDatabase();
            ChatBlock.sendMessage(player, ChatColor.AQUA + "Configuration reloaded");
            return;
        }
    }
}
