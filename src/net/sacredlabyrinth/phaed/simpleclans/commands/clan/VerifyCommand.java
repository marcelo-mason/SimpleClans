package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
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
     * Execute the command
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg)
    {
        SimpleClans plugin = SimpleClans.getInstance();

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
        Clan clan = cp == null ? null : cp.getClan();

        boolean isNonVerified = clan != null && !clan.isVerified();
        boolean isBuyer = isNonVerified && plugin.getSettingsManager().isRequireVerification() && plugin.getSettingsManager().isePurchaseVerification();

        if (isBuyer)
        {
            if (arg.length == 0)
            {
                if (clan != null)
                {
                    if (plugin.getClanManager().purchaseVerification(player))
                    {
                        clan.verifyClan();
                        clan.addBb(player.getName(), ChatColor.AQUA + "Clan " + clan.getName() + " has been verified!");
                        ChatBlock.sendMessage(player, ChatColor.AQUA + "The clan has been verified");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "The clan does not exist");
                }
            }
        }
        else if (plugin.getPermissionsManager().has(player, "simpleclans.mod.verify"))
        {
            if (arg.length == 1)
            {
                Clan cclan = plugin.getClanManager().getClan(arg[0]);

                if (cclan != null)
                {
                    if (!cclan.isVerified())
                    {
                        cclan.verifyClan();
                        cclan.addBb(player.getName(), ChatColor.AQUA + "Clan " + cclan.getName() + " has been verified!");
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
                ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /clan verify [tag]");
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
        }
    }
}
