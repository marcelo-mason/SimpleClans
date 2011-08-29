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
public class RivalCommand
{
    public RivalCommand()
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

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.rival"))
        {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null)
            {
                Clan clan = cp.getClan();

                if (clan.isVerified())
                {
                    if (!clan.isUnrivable())
                    {
                        if (!clan.reachedRivalLimit())
                        {
                            if (clan.isLeader(player))
                            {
                                if (arg.length == 2)
                                {
                                    if (clan.getSize() >= plugin.getSettingsManager().getClanMinSizeToRival())
                                    {
                                        String action = arg[0];
                                        Clan rival = plugin.getClanManager().getClan(arg[1]);

                                        if (rival != null)
                                        {
                                            if (!plugin.getSettingsManager().isUnrivable(rival.getTag()))
                                            {
                                                if (rival.isVerified())
                                                {
                                                    if (action.equals("add"))
                                                    {
                                                        if (!clan.isRival(rival.getTag()))
                                                        {
                                                            clan.addRival(rival);
                                                            rival.addBb(player.getName(), ChatColor.AQUA + Helper.capitalize(clan.getName()) + " has initiated a rivalry with " + rival.getName());
                                                            clan.addBb(player.getName(), ChatColor.AQUA + Helper.capitalize(player.getName()) + " has initiated a rivalry with " + Helper.capitalize(rival.getName()));
                                                        }
                                                        else
                                                        {
                                                            ChatBlock.sendMessage(player, ChatColor.RED + "Your clans are already rivals");
                                                        }
                                                    }
                                                    else if (action.equals("remove"))
                                                    {
                                                        if (clan.isRival(rival.getTag()))
                                                        {
                                                            plugin.getRequestManager().addRivalryBreakRequest(plugin, cp, rival, clan);
                                                            ChatBlock.sendMessage(player, ChatColor.AQUA + Helper.capitalize(rival.getName()) + " leaders have been asked to end the rivalry");
                                                        }
                                                        else
                                                        {
                                                            ChatBlock.sendMessage(player, ChatColor.RED + "Your clans are not rivals");
                                                        }
                                                    }
                                                    else
                                                    {
                                                        ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " ally add/remove [tag]");
                                                    }
                                                }
                                                else
                                                {
                                                    ChatBlock.sendMessage(player, ChatColor.RED + "You cannot rival an unverified clan");
                                                }
                                            }
                                            else
                                            {
                                                ChatBlock.sendMessage(player, ChatColor.RED + "No clan matched");
                                            }
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + "No clan matched");
                                        }
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + "Your clan must have at least " + plugin.getSettingsManager().getClanMinSizeToRival() + " players in order to make rivalries");
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " rival add/remove [tag]");
                                }
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + "You do not have leader permissions");
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + "Your clan has reached the rival limit of " + plugin.getSettingsManager().getRivalLimitPercent() + "% of all clans.  You canno initiate any more rivalries.");
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "Your clan cannot create rivals");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Clan is not verified");
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
