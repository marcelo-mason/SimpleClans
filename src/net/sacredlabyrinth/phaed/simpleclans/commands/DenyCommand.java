package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.util.logging.Level;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
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
public class DenyCommand implements CommandExecutor
{
    private SimpleClans plugin;

    /**
     *
     */
    public DenyCommand()
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
                processDeny(player);
                return true;
            }
        }
        catch (Exception ex)
        {
            SimpleClans.log(Level.SEVERE, "Command failure: {0}", ex.getMessage());
        }

        return false;
    }

    private void processDeny(Player player)
    {
        if (plugin.getSettingsManager().isBanned(player.getName()))
        {
            ChatBlock.sendMessage(player, ChatColor.RED + "You are banned from using " + plugin.getSettingsManager().getCommandClan() + " commands");
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp != null)
        {
            Clan clan = cp.getClan();

            if (clan.isLeader(player))
            {
                if (plugin.getRequestManager().hasRequest(clan.getTag()))
                {
                    if (cp.getVote() == null)
                    {
                        plugin.getRequestManager().deny(cp);
                        clan.leaderAnnounce(player.getName(), ChatColor.RED + Helper.capitalize(player.getName()) + " has voted to deny");
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "You have already voted");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Nothing to deny");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "You do not have leader permissions");
            }
        }
        else
        {
            if (plugin.getRequestManager().hasRequest(player.getName().toLowerCase()))
            {
                cp = plugin.getClanManager().getCreateClanPlayer(player.getName());
                plugin.getRequestManager().deny(cp);
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Nothing to deny");
            }
        }
    }
}
