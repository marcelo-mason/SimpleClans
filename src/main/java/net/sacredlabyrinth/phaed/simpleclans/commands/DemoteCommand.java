package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.beta.GenericPlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class DemoteCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public DemoteCommand(SimpleClans plugin)
    {
        super("Demote");
        this.plugin = plugin;
        setArgumentRange(1, 1);
        setUsages(String.format(plugin.getLang("usage.emote"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("demote.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp != null) {
            if (cp.isLeader() && plugin.getPermissionsManager().has(sender, "simpleclans.leader.demote")) {
                return MessageFormat.format(plugin.getLang("0.demote.leader.1.demote.a.leader.to.member"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
            }
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.demote")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isLeader(player)) {
                    if (args.length == 1) {
                        String demotedName = args[0];

                        if (clan.allOtherLeadersOnline(demotedName)) {
                            if (clan.isLeader(demotedName)) {
                                if (clan.getLeaders().size() == 1 || !plugin.getSettingsManager().isConfirmationForDemote()) {
                                    clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("demoted.back.to.member"), Helper.capitalize(demotedName)));
                                    clan.demote(demotedName);
                                } else {
                                    plugin.getRequestManager().addDemoteRequest(cp, demotedName, clan);
                                    ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("demotion.vote.has.been.requested.from.all.leaders"));
                                }
                            } else {
                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("player.is.not.a.leader.of.your.clan"));
                            }
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("leaders.must.be.online.to.vote.on.demotion"));
                        }
                    }
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
