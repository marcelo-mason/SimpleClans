package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.util.logging.Level;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class MoreCommand implements CommandExecutor
{
    private SimpleClans plugin;

    /**
     *
     */
    public MoreCommand()
    {
        plugin = SimpleClans.getInstance();
    }

    /**
     *
     * @param sender
     * @param command
     * @param label
     * @param args
     * @return
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        try
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                processMore(player);
                return true;
            }
        }
        catch (Exception ex)
        {
            SimpleClans.log(Level.SEVERE, "Command failure: {0}", ex.getMessage());
        }

        return false;
    }

    private void processMore(Player player)
    {
        if (plugin.getSettingsManager().isBanned(player.getName()))
        {
            ChatBlock.sendMessage(player, ChatColor.RED + "You are banned from using " + plugin.getSettingsManager().getCommandClan() + " commands");
            return;
        }

        ChatBlock chatBlock = plugin.getStorageManager().getChatBlock(player);

        if (chatBlock != null && chatBlock.size() > 0)
        {
            chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

            if (chatBlock.size() > 0)
            {
                ChatBlock.sendBlank(player);
                ChatBlock.sendMessage(player, plugin.getSettingsManager().getPageHeadingsColor() + "Type /" + plugin.getSettingsManager().getCommandMore() + " to view next page.");
            }
            ChatBlock.sendBlank(player);
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + "Nothing more to see.");
        }
    }
}
