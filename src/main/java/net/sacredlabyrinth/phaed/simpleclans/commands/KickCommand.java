package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class KickCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public KickCommand(SimpleClans plugin)
    {
        super("Kick");
        this.plugin = plugin;
        setArgumentRange(1, 1);
        setUsages(MessageFormat.format(plugin.getLang("usage.kick"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("kick.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp != null) {
            if (cp.isLeader()) {
                if (plugin.getPermissionsManager().has(sender, "simpleclans.leader.kick")) {
                    return MessageFormat.format(plugin.getLang("0.kick.player.1.kick.a.player.from.the.clan"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
                }
            }
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.kick")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isLeader(player)) {
                    String kicked = args[0];

                    if (kicked != null) {
                        if (!kicked.equals(player.getName())) {
                            if (clan.isMember(kicked)) {
                                if (!clan.isLeader(kicked)) {
                                    clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.been.kicked.by"), Helper.capitalize(kicked), player.getName()));
                                    clan.removePlayerFromClan(kicked);
                                } else {
                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("you.cannot.kick.another.leader"));
                                }
                            } else {
                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("the.player.is.not.a.member.of.your.clan"));
                            }
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("you.cannot.kick.yourself"));
                        }
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.matched"));
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
