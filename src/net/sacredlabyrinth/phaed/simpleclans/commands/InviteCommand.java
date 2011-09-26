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
public class InviteCommand
{
    public InviteCommand()
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

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.invite"))
        {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null)
            {
                Clan clan = cp.getClan();

                if (clan.isLeader(player))
                {
                    if (arg.length == 1)
                    {
                        Player invited = Helper.matchOnePlayer(arg[0]);

                        if (invited != null)
                        {
                            if (plugin.getPermissionsManager().has(invited, "simpleclans.member.can-join"))
                            {
                                if (!invited.getName().equals(player.getName()))
                                {
                                    if (!plugin.getSettingsManager().isBanned(player.getName()))
                                    {
                                        ClanPlayer cpInv = plugin.getClanManager().getClanPlayer(invited);

                                        if (cpInv == null)
                                        {
                                            plugin.getRequestManager().addInviteRequest(cp, invited.getName(), clan);
                                            ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang().getString("has.been.asked.to.join"), Helper.capitalize(invited.getName()), clan.getName()));
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("the.player.is.already.member.of.another.clan"));
                                        }
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("this.player.is.banned.from.using.clan.commands"));
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("you.cannot.invite.yourself"));
                                }
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("the.player.doesn.t.not.have.the.permissions.to.join.clans"));
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("no.player.matched"));
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang().getString("usage.0.invite.player"), plugin.getSettingsManager().getCommandClan()));
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
}
