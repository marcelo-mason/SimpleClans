package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import java.util.List;
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
public class AllyCommand
{
    public AllyCommand()
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

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.ally"))
        {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null)
            {
                Clan clan = cp.getClan();

                if (clan.isVerified())
                {
                    if (clan.isLeader(player))
                    {
                        if (arg.length == 2)
                        {
                            if (clan.getSize() >= plugin.getSettingsManager().getClanMinSizeToAlly())
                            {
                                String action = arg[0];
                                Clan ally = plugin.getClanManager().getClan(arg[1]);

                                if (ally != null)
                                {
                                    if (ally.isVerified())
                                    {
                                        if (action.equals("add"))
                                        {
                                            if (!clan.isAlly(ally.getTag()))
                                            {
                                                List<ClanPlayer> onlineLeaders = Helper.stripOffLinePlayers(clan.getLeaders());

                                                if (!onlineLeaders.isEmpty())
                                                {
                                                    plugin.getRequestManager().addAllyRequest(plugin, cp, ally, clan);
                                                    ChatBlock.sendMessage(player, ChatColor.AQUA + Helper.capitalize(ally.getName()) + " leaders have been asked for an alliance");
                                                }
                                                else
                                                {
                                                    ChatBlock.sendMessage(player, ChatColor.RED + "At least one leader of the allied must be online to accept the alliance");
                                                }
                                            }
                                            else
                                            {
                                                ChatBlock.sendMessage(player, ChatColor.RED + "Your clans are already allies");
                                            }
                                        }
                                        else if (action.equals("remove"))
                                        {
                                            if (clan.isAlly(ally.getTag()))
                                            {
                                                clan.removeAlly(ally);
                                                ally.addBb(cp.getName(), ChatColor.AQUA + Helper.capitalize(clan.getName()) + " has broken the alliance with " + ally.getName());
                                                clan.addBb(cp.getName(), ChatColor.AQUA + Helper.capitalize(cp.getName()) + " has broken the alliance with " + Helper.capitalize(ally.getName()));
                                            }
                                            else
                                            {
                                                ChatBlock.sendMessage(player, ChatColor.RED + "Your clans are not allies");
                                            }
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /clan ally add/remove [tag]");
                                        }
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + "You cannot ally with an unverified clan");
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + "No clan matched");
                                }
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + "Your clan must have at least " + plugin.getSettingsManager().getClanMinSizeToAlly() + " players in order to make alliances");
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /clan ally add/remove [tag]");
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "You do not have leader permissions");
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
