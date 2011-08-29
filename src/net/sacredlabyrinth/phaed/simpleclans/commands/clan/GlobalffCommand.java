package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class GlobalffCommand
{
    public GlobalffCommand()
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

        if (arg.length == 1)
        {
            String action = arg[0];

            if (action.equalsIgnoreCase("allow"))
            {
                if (plugin.getSettingsManager().isGlobalff())
                {
                    ChatBlock.sendMessage(player, ChatColor.AQUA + "Global friendly-fire is already being allowed");
                }
                else
                {
                    plugin.getSettingsManager().setGlobalff(true);
                    ChatBlock.sendMessage(player, ChatColor.AQUA + "Global friendly-fire is set to allowed");
                }
            }
            else if (action.equalsIgnoreCase("auto"))
            {
                if (plugin.getSettingsManager().isGlobalff())
                {
                    ChatBlock.sendMessage(player, ChatColor.AQUA + "Global friendy-fire is already being managed by each clan");
                }
                else
                {
                    plugin.getSettingsManager().setGlobalff(false);
                    ChatBlock.sendMessage(player, ChatColor.AQUA + "Global friendy-fire is now managed by each clan");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " globalff allow/auto");
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " ff allow/auto");
        }
    }
}
