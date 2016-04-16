package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * @author phaed
 */
public class RivalCommand
{
    public RivalCommand()
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
                                                if (action.equals(plugin.getLang("add")))
                                                {
                                                    if (!clan.reachedRivalLimit())
                                                    {
                                                        if (!clan.isRival(rival.getTag()))
                                                        {
                                                            clan.addRival(rival);
                                                            rival.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.initiated.a.rivalry"), Helper.capitalize(clan.getName()), rival.getName()));
                                                            clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.initiated.a.rivalry"), Helper.capitalize(player.getName()), Helper.capitalize(rival.getName())));
                                                        }
                                                        else
                                                        {
                                                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("your.clans.are.already.rivals"));
                                                        }
                                                    }
                                                    else
                                                    {
                                                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("rival.limit.reached"));
                                                    }

                                                }
                                                else if (action.equals(plugin.getLang("remove")))
                                                {
                                                    if (clan.isRival(rival.getTag()))
                                                    {
                                                        plugin.getRequestManager().addRivalryBreakRequest(cp, rival, clan);
                                                        ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang("leaders.asked.to.end.rivalry"), Helper.capitalize(rival.getName())));
                                                    }
                                                    else
                                                    {
                                                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("your.clans.are.not.rivals"));
                                                    }
                                                }
                                                else
                                                {
                                                    ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.rival"), plugin.getSettingsManager().getCommandClan()));
                                                }
                                            }
                                            else
                                            {
                                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("cannot.rival.an.unverified.clan"));
                                            }
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("the.clan.cannot.be.rivaled"));
                                        }
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.clan.matched"));
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("min.players.rivalries"), plugin.getSettingsManager().getClanMinSizeToRival()));
                                }
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.rival"), plugin.getSettingsManager().getCommandClan()));
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("your.clan.cannot.create.rivals"));
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.is.not.verified"));
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
