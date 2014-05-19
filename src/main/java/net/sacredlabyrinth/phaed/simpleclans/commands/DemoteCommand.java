package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.UUID;
import net.sacredlabyrinth.phaed.simpleclans.api.UUIDMigration;

/**
 *
 * @author phaed
 */
public class DemoteCommand
{
    public DemoteCommand()
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

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.demote"))
        {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null)
            {
                Clan clan = cp.getClan();

                if (clan.isLeader(player))
                {
                    if (arg.length == 1)
                    {
                        String demotedName = arg[0];
                        boolean allOtherLeadersOnline;
                        
                        if (demotedName == null) 
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.matched"));
                            return;
                        }
                        
                        if (SimpleClans.getInstance().hasUUID())
                        {
                            UUID PlayerUniqueId = UUIDMigration.getForcedPlayerUUID(demotedName);
                            if (PlayerUniqueId == null)
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.matched"));
                                return;
                            }
                            allOtherLeadersOnline = clan.allOtherLeadersOnline(PlayerUniqueId);
                        } else 
                        {
                            allOtherLeadersOnline = clan.allOtherLeadersOnline(demotedName);
                        }

                        if (allOtherLeadersOnline)
                        {
                            if (SimpleClans.getInstance().hasUUID())
                            {
                                UUID PlayerUniqueId = UUIDMigration.getForcedPlayerUUID(demotedName);
                                if (clan.isLeader(PlayerUniqueId))
                                {
                                    if (clan.getLeaders().size() == 1|| !plugin.getSettingsManager().isConfirmationForDemote())
                                    {
                                        clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("demoted.back.to.member"), Helper.capitalize(demotedName)));
                                        clan.demote(PlayerUniqueId);
                                    }
                                    else
                                    {
                                        plugin.getRequestManager().addDemoteRequest(cp, demotedName, clan);
                                        ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("demotion.vote.has.been.requested.from.all.leaders"));
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("player.is.not.a.leader.of.your.clan"));
                                }
                            } else 
                            {
                                if (clan.isLeader(demotedName))
                                {
                                    if (clan.getLeaders().size() == 1|| !plugin.getSettingsManager().isConfirmationForDemote())
                                    {
                                        clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("demoted.back.to.member"), Helper.capitalize(demotedName)));
                                        clan.demote(demotedName);
                                    }
                                    else
                                    {
                                        plugin.getRequestManager().addDemoteRequest(cp, demotedName, clan);
                                        ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("demotion.vote.has.been.requested.from.all.leaders"));
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("player.is.not.a.leader.of.your.clan"));
                                }
                            }

                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("leaders.must.be.online.to.vote.on.demotion"));
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.demote.leader"), plugin.getSettingsManager().getCommandClan()));
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
