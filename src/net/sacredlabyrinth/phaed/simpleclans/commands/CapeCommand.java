package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * @author phaed
 */
public class CapeCommand
{
    public CapeCommand()
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

        if (arg.length == 1 && arg[0].equalsIgnoreCase("toggle"))
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.cape-toggle"))
            {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp != null)
                {
                    Clan clan = cp.getClan();

                    if (clan.isVerified())
                    {
                        if (cp.isCapeEnabled())
                        {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang().getString("capeoff"));
                            cp.setCapeEnabled(false);
                            plugin.getSpoutPluginManager().clearCape(player);
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang().getString("capeon"));
                            cp.setCapeEnabled(true);
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
        else
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.leader.cape"))
            {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp != null)
                {
                    Clan clan = cp.getClan();

                    if (clan.isVerified())
                    {
                        if (clan.isLeader(player))
                        {
                            if (arg.length == 1)
                            {
                                String url = arg[0];

                                if (url.contains(".png"))
                                {
                                    if (Helper.testURL(url))
                                    {
                                        clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang().getString("changed.the.clan.cape"), Helper.capitalize(player.getName())));
                                        clan.setClanCape(url);
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("url.error"));
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("cape.must.be.png"));
                                }
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang().getString("usage.cape.url"), plugin.getSettingsManager().getCommandClan()));
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
}
