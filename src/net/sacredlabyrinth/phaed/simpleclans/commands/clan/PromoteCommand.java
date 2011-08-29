package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class PromoteCommand
{
    public PromoteCommand()
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

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.promote"))
        {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null)
            {
                Clan clan = cp.getClan();

                if (clan.isLeader(player))
                {
                    if (arg.length == 1)
                    {
                        Player promoted = Helper.matchOnePlayer(arg[0]);

                        if (promoted != null)
                        {
                            if (plugin.getPermissionsManager().has(promoted, "simpleclans.leader.promotable"))
                            {
                                if (!promoted.getName().equals(player.getName()))
                                {
                                    if (clan.allLeadersOnline())
                                    {
                                        if (clan.isMember(promoted))
                                        {
                                            if (!clan.isLeader(promoted))
                                            {
                                                if (clan.getLeaders().size() == 1)
                                                {
                                                    clan.addBb(player.getName(), ChatColor.AQUA + Helper.capitalize(promoted.getName()) + " has been promoted to leader");
                                                    clan.promote(promoted.getName());
                                                }
                                                else
                                                {
                                                    plugin.getRequestManager().addPromoteRequest(plugin, cp, promoted.getName(), clan);
                                                    ChatBlock.sendMessage(player, ChatColor.AQUA + "Promotion vote has been requested from all leaders");
                                                }
                                            }
                                            else
                                            {
                                                ChatBlock.sendMessage(player, ChatColor.RED + "The player is already a leader");
                                            }
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + "The player is not a member of your clan");
                                        }
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + "All leaders must be online to vote on this promotion");
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + "You cannot promote yourself");
                                }
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + "The player does not have the permissions to lead a clan");
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + "The member to be promoted must be online");
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " promote <member>");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "You do not have leader permissions");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "You are not a member of any clan");
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
        }
    }
}
