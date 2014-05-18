package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 *
 * @author phaed
 */
public class UntrustCommand
{
    public UntrustCommand()
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

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.settrust"))
        {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null)
            {
                Clan clan = cp.getClan();

                if (clan.isLeader(player))
                {
                    if (arg.length == 1)
                    {
                        String trusted = arg[0];

                        if (trusted != null)
                        {
                            if (!trusted.equals(player.getName()))
                            {
                                if (clan.isMember(trusted))
                                {
                                    if (!clan.isLeader(trusted))
                                    {
                                        ClanPlayer tcp = plugin.getClanManager().getClanPlayerName(trusted);
                                        if (tcp == null) 
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.matched"));
                                            return;
                                        }
                                        
                                        if (tcp.isTrusted())
                                        {
                                            clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.been.given.untrusted.status.by"), Helper.capitalize(trusted), player.getName()));
                                            tcp.setTrusted(false);
                                            plugin.getStorageManager().updateClanPlayer(tcp);
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("this.player.is.already.untrusted"));
                                        }
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("leaders.cannot.be.untrusted"));
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("the.player.is.not.a.member.of.your.clan"));
                                }
                            }
                            else
                            {
                                ChatBlock.sendMessage(player,  ChatColor.RED + plugin.getLang("you.cannot.untrust.yourself"));
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.matched"));
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.0.untrust.player"), plugin.getSettingsManager().getCommandClan()));
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
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
