package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class VerifyCommand
{
    public VerifyCommand()
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

        if (plugin.getPermissionsManager().has(player, "simpleclans.mod.verify"))
        {
            if (arg.length == 1)
            {
                Clan clan = plugin.getClanManager().getClan(arg[0]);

                if (clan != null)
                {
                    if (!clan.isVerified())
                    {
                        clan.verifyClan();
                        clan.addBb(player.getName(), ChatColor.AQUA + "Clan " + clan.getName() + " has been verified!");
                        ChatBlock.sendMessage(player, ChatColor.AQUA + "The clan has been verified");
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "The clan is already verified");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "The clan does not exist");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " verify [tag]");
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
        }
    }
}
