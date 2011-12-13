package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class BbCommand
{
    public BbCommand()
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

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp != null)
        {
            Clan clan = cp.getClan();

            if (clan.isVerified())
            {
                if (arg.length == 0)
                {
                    if (plugin.getPermissionsManager().has(player, "simpleclans.member.bb"))
                    {
                        clan.displayBb(player);
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("insufficient.permissions"));
                    }
                }
                else
                {
                    if (plugin.getPermissionsManager().has(player, "simpleclans.member.bb-add"))
                    {
                        if (cp.isTrusted())
                        {
                            String msg = Helper.toMessage(arg);
                            clan.addBb(player.getName(), ChatColor.AQUA + player.getName() + ": " + ChatColor.WHITE + msg);
                            plugin.getStorageManager().updateClan(clan);
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("no.leader.permissions"));
                        }
                    }
                    else if (plugin.getPermissionsManager().has(player, "simpleclans.member.bb-toggle"))
                    {
                        if (cp.isBbEnabled())
                        {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang().getString("bboff"));
                            cp.setBbEnabled(false);
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang().getString("bbon"));
                            cp.setBbEnabled(true);
                        }
                        plugin.getStorageManager().updateClanPlayer(cp);
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("insufficient.permissions"));
                    }
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
}