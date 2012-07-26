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
public class RivalCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public RivalCommand(SimpleClans plugin)
    {
        super("Rival");
        this.plugin = plugin;
        setArgumentRange(2, 2);
        setUsages(MessageFormat.format(plugin.getLang("usage.rival"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("rival.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp != null) {
            if (cp.isLeader() && cp.getClan().isVerified() && plugin.getPermissionsManager().has(sender, "simpleclans.leader.rival")) {
                return MessageFormat.format(plugin.getLang("0.rival.add.remove.tag.1.add.remove.a.rival.clan"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
            }
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.rival")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isVerified()) {
                    if (!clan.isUnrivable()) {
                        if (clan.isLeader(player)) {

                            if (clan.getSize() >= plugin.getSettingsManager().getClanMinSizeToRival()) {
                                String action = args[0];
                                Clan rival = plugin.getClanManager().getClan(args[1]);

                                if (rival != null) {
                                    if (!plugin.getSettingsManager().isUnrivable(rival.getTag())) {
                                        if (rival.isVerified()) {
                                            if (action.equals(plugin.getLang("add"))) {
                                                if (!clan.reachedRivalLimit()) {
                                                    if (!clan.isRival(rival.getTag())) {
                                                        clan.addRival(rival);
                                                        rival.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.initiated.a.rivalry"), Helper.capitalize(clan.getName()), rival.getName()));
                                                        clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.initiated.a.rivalry"), Helper.capitalize(player.getName()), Helper.capitalize(rival.getName())));
                                                    } else {
                                                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("your.clans.are.already.rivals"));
                                                    }
                                                } else {
                                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("rival.limit.reached"));
                                                }

                                            } else if (action.equals(plugin.getLang("remove"))) {
                                                if (clan.isRival(rival.getTag())) {
                                                    plugin.getRequestManager().addRivalryBreakRequest(cp, rival, clan);
                                                    ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang("leaders.asked.to.end.rivalry"), Helper.capitalize(rival.getName())));
                                                } else {
                                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("your.clans.are.not.rivals"));
                                                }
                                            } else {
                                                ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.ally"), plugin.getSettingsManager().getCommandClan()));
                                            }
                                        } else {
                                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("cannot.rival.an.unverified.clan"));
                                        }
                                    } else {
                                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("the.clan.cannot.be.rivaled"));
                                    }
                                } else {
                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.clan.matched"));
                                }
                            } else {
                                ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("min.players.rivalries"), plugin.getSettingsManager().getClanMinSizeToRival()));
                            }
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                        }
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("your.clan.cannot.create.rivals"));
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
