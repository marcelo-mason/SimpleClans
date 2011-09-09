package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * @author phaed
 */
public class DisbandCommand
{
    public DisbandCommand()
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

        if (arg.length == 0)
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.leader.disband"))
            {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp != null)
                {
                    Clan clan = cp.getClan();

                    if (clan.isLeader(player))
                    {
                        if (clan.getLeaders().size() == 1)
                        {
                            clan.clanAnnounce(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang().getString("clan.has.been.disbanded"), clan.getName()));
                            clan.disband();
                        }
                        else
                        {
                            plugin.getRequestManager().addDisbandRequest(cp, clan);
                            ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang().getString("clan.disband.vote.has.been.requested.from.all.leaders"));
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("no.leader.permissions"));
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
        else if (arg.length == 1)
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.mod.disband"))
            {
                Clan clan = plugin.getClanManager().getClan(arg[0]);

                if (clan != null)
                {
                    plugin.getClanManager().serverAnnounce(ChatColor.AQUA + MessageFormat.format(plugin.getLang().getString("clan.has.been.disbanded"), clan.getName()));
                    clan.disband();
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("no.clan.matched"));
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("insufficient.permissions"));
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang().getString("usage.0.disband"), plugin.getSettingsManager().getCommandClan()));
        }
    }
}
