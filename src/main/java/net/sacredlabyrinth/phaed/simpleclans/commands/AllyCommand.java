package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.List;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.beta.GenericPlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class AllyCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public AllyCommand(SimpleClans plugin)
    {
        super("Ally");
        this.plugin = plugin;
        setArgumentRange(2, 2);
        setUsages(MessageFormat.format(plugin.getLang("usage.ally"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("ally.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp != null) {
            if (plugin.getPermissionsManager().has(sender, "simpleclans.leader.ally")) {
                if (cp.isLeader() && cp.getClan().isVerified()) {
                    return MessageFormat.format(plugin.getLang("0.ally.add.remove.tag.1.add.remove.an.ally.clan"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
                }
            }
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {
        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.ally")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isVerified()) {
                    if (clan.isLeader(player)) {
                        if (clan.getSize() >= plugin.getSettingsManager().getClanMinSizeToAlly()) {
                            String action = args[0];
                            Clan ally = plugin.getClanManager().getClan(args[1]);

                            if (ally != null) {
                                if (ally.isVerified()) {
                                    if (action.equals(plugin.getLang("add"))) {
                                        if (!clan.isAlly(ally.getTag())) {
                                            List<ClanPlayer> onlineLeaders = Helper.stripOffLinePlayers(clan.getLeaders());

                                            if (!onlineLeaders.isEmpty()) {
                                                plugin.getRequestManager().addAllyRequest(cp, ally, clan);
                                                ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang("leaders.have.been.asked.for.an.alliance"), Helper.capitalize(ally.getName())));
                                            } else {
                                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("at.least.one.leader.accept.the.alliance"));
                                            }
                                        } else {
                                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("your.clans.are.already.allies"));
                                        }
                                    } else if (action.equals(plugin.getLang("remove"))) {
                                        if (clan.isAlly(ally.getTag())) {
                                            clan.removeAlly(ally);
                                            ally.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.broken.the.alliance"), Helper.capitalize(clan.getName()), ally.getName()));
                                            clan.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.broken.the.alliance"), Helper.capitalize(cp.getName()), Helper.capitalize(ally.getName())));
                                        } else {
                                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("your.clans.are.not.allies"));
                                        }
                                    } else {
                                        ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.ally"), plugin.getSettingsManager().getCommandClan()));
                                    }
                                } else {
                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("cannot.ally.with.an.unverified.clan"));
                                }
                            } else {
                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.clan.matched"));
                            }
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("minimum.to.make.alliance"), plugin.getSettingsManager().getClanMinSizeToAlly()));
                        }

                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                    }
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.is.not.verified"));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
