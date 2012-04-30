package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author phaed
 */
public class AllyCommand
{
    public AllyCommand()
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
                                        if (action.equals(plugin.getLang("add")))
                                        {
                                            if (!clan.isAlly(ally.getTag()))
                                            {
                                                List<ClanPlayer> onlineLeaders = Helper.stripOffLinePlayers(clan.getLeaders());

                                                if (!onlineLeaders.isEmpty())
                                                {
                                                    plugin.getRequestManager().addAllyRequest(cp, ally, clan);
                                                    ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang("leaders.have.been.asked.for.an.alliance"), Helper.capitalize(ally.getName())));
                                                }
                                                else
                                                {
                                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("at.least.one.leader.accept.the.alliance"));
                                                }
                                            }
                                            else
                                            {
                                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("your.clans.are.already.allies"));
                                            }
                                        }
                                        else if (action.equals(plugin.getLang("remove")))
                                        {
                                            if (clan.isAlly(ally.getTag()))
                                            {
                                                clan.removeAlly(ally);
                                                ally.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.broken.the.alliance"), Helper.capitalize(clan.getName()), ally.getName()));
                                                clan.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.broken.the.alliance"), Helper.capitalize(cp.getName()), Helper.capitalize(ally.getName())));
                                            }
                                            else
                                            {
                                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("your.clans.are.not.allies"));
                                            }
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.ally"), plugin.getSettingsManager().getCommandClan()));
                                        }
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("cannot.ally.with.an.unverified.clan"));
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.clan.matched"));
                                }
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("minimum.to.make.alliance"), plugin.getSettingsManager().getClanMinSizeToAlly()));
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.ally"), plugin.getSettingsManager().getCommandClan()));
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
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
