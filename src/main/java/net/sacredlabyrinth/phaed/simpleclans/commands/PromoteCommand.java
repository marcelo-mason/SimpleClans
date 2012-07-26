package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.beta.GenericPlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class PromoteCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public PromoteCommand(SimpleClans plugin)
    {
        super("Promote");
        this.plugin = plugin;
        setArgumentRange(0, 0);
        setUsages(MessageFormat.format(plugin.getLang("usage.promote"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("promote.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp != null) {
            if (cp.isLeader() && plugin.getPermissionsManager().has(sender, "simpleclans.leader.promote")) {
                return MessageFormat.format(plugin.getLang("0.promote.member.1.promote.a.member.to.leader"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
            }
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.promote")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isLeader(player)) {

                    Player promoted = Helper.matchOnePlayer(args[0]);

                    if (promoted != null) {
                        if (plugin.getPermissionsManager().has(promoted, "simpleclans.leader.promotable")) {
                            if (!promoted.getName().equals(player.getName())) {
                                if (clan.allLeadersOnline()) {
                                    if (clan.isMember(promoted)) {
                                        if (!clan.isLeader(promoted) || !plugin.getSettingsManager().isConfirmationForPromote()) {
                                            clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("promoted.to.leader"), Helper.capitalize(promoted.getName())));
                                            clan.promote(promoted.getName());
                                        } else {
                                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("the.player.is.already.a.leader"));
                                        }
                                    } else {
                                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("the.player.is.not.a.member.of.your.clan"));
                                    }
                                } else {
                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("all.leaders.must.be.online.to.vote.on.this.promotion"));
                                }
                            } else {
                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("you.cannot.promote.yourself"));
                            }
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("the.player.does.not.have.the.permissions.to.lead.a.clan"));
                        }
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("the.member.to.be.promoted.must.be.online"));
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
