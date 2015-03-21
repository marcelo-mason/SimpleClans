package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;
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
                        Player invited;
                        if (SimpleClans.getInstance().hasUUID())
                        {
                            invited = SimpleClans.getInstance().getServer().getPlayer(UUIDMigration.getForcedPlayerUUID(arg[0]));
                        } else
                        {
                            invited = SimpleClans.getInstance().getServer().getPlayerExact(arg[0]);
                        }

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
                                            if (plugin.getClanManager().purchaseInvite(player))
                                            {
                                                plugin.getRequestManager().addInviteRequest(cp, invited.getName(), clan);
                                                ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.been.asked.to.join"), Helper.capitalize(invited.getName()), clan.getName()));
                                            }
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("the.player.is.already.member.of.another.clan"));
                                        }
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("this.player.is.banned.from.using.clan.commands"));
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("you.cannot.invite.yourself"));
                                }
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("the.player.doesn.t.not.have.the.permissions.to.join.clans"));
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.matched"));
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.0.invite.player"), plugin.getSettingsManager().getCommandClan()));
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
