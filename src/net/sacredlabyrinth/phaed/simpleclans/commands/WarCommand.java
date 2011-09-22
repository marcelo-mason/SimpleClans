package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author phaed
 */
public class WarCommand
{
    public WarCommand()
    {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg)
    {
        SimpleClans plugin = SimpleClans.getInstance();

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.war"))
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
                            String action = arg[0];
                            Clan war = plugin.getClanManager().getClan(arg[1]);

                            if (war != null)
                            {
                                if (clan.isRival(war.getTag()))
                                {
                                    if (action.equals(plugin.getLang().getString("start")))
                                    {
                                        if (!clan.isWarring(war.getTag()))
                                        {
                                            List<ClanPlayer> onlineLeaders = Helper.stripOffLinePlayers(clan.getLeaders());

                                            if (!onlineLeaders.isEmpty())
                                            {
                                                plugin.getRequestManager().addWarStartRequest(cp, war, clan);
                                                ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang().getString("leaders.have.been.asked.to.accept.the.war.request"), Helper.capitalize(war.getName())));
                                            }
                                            else
                                            {
                                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("at.least.one.leader.accept.the.alliance"));
                                            }
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("clans.already.at.war"));
                                        }
                                    }
                                    else if (action.equals(plugin.getLang().getString("end")))
                                    {
                                        if (clan.isWarring(war.getTag()))
                                        {
                                            plugin.getRequestManager().addWarEndRequest(cp, war, clan);
                                            ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang().getString("leaders.asked.to.end.rivalry"), Helper.capitalize(war.getName())));
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("clans.not.at.war"));
                                        }
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang().getString("usage.war"), plugin.getSettingsManager().getCommandClan()));
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("you.can.only.start.war.with.rivals"));
                                }
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("no.clan.matched"));
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang().getString("usage.war"), plugin.getSettingsManager().getCommandClan()));
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("no.leader.permissions"));
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("clan.is.not.verified"));
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("not.a.member.of.any.clan"));
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("insufficient.permissions"));
        }
    }
}
